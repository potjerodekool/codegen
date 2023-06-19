package io.github.potjerodekool.codegen.loader.asm.visitor;

import io.github.potjerodekool.codegen.loader.TypeElementLoader;
import io.github.potjerodekool.codegen.loader.asm.visitor.signature2.RootSignatureVisitor2;
import io.github.potjerodekool.codegen.model.element.ElementKind;
import io.github.potjerodekool.codegen.model.element.Name;
import io.github.potjerodekool.codegen.model.element.NestingKind;
import io.github.potjerodekool.codegen.model.element.TypeElement;
import io.github.potjerodekool.codegen.model.symbol.ClassSymbol;
import io.github.potjerodekool.codegen.model.symbol.ErrorSymbol;
import io.github.potjerodekool.codegen.model.symbol.PackageSymbol;
import io.github.potjerodekool.codegen.model.util.QualifiedName;
import io.github.potjerodekool.codegen.model.util.SymbolTable;
import io.github.potjerodekool.codegen.model.util.type.Types;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;

public class AsmClassVisitor extends ClassVisitor {

    private ClassSymbol classSymbol;
    private final TypeElementLoader loader;
    private final Types types;
    private final SymbolTable symbolTable;

    public AsmClassVisitor(final int api,
                           final TypeElementLoader loader,
                           final Types types,
                           final SymbolTable symbolTable) {
        super(api);
        this.loader = loader;
        this.types = types;
        this.symbolTable = symbolTable;
    }

    public ClassSymbol getClassSymbol() {
        return classSymbol;
    }

    @Override
    public void visit(final int version,
                      final int access,
                      final String name,
                      final String signature,
                      final String superName,
                      final String[] interfaces) {
        final var internalClassNameSeparatorIndex = name.lastIndexOf("$");

        if (internalClassNameSeparatorIndex > -1) {
            final var enclosingElementName = name.substring(0, internalClassNameSeparatorIndex).replace('/', '.');
            final TypeElement enclosingElement = loader.loadTypeElement(enclosingElementName);
            final var className = Type.getObjectType(name).getClassName();
            final var simpleName = Name.of(className.substring(internalClassNameSeparatorIndex + 1));
            this.classSymbol = ClassSymbol.create(getElementKind(access), simpleName, NestingKind.MEMBER, enclosingElement);
        } else {
            final var className = Type.getObjectType(name).getClassName();
            final var qualifiedName = QualifiedName.from(className);
            final var packageSymbol = symbolTable.findOrCreatePackageSymbol(qualifiedName.packageName());
            this.classSymbol = ClassSymbol.create(getElementKind(access), qualifiedName.simpleName(), NestingKind.TOP_LEVEL, packageSymbol);
        }

        symbolTable.addClass(classSymbol);

        if (signature != null) {
            new SignatureReader(signature).accept(new RootSignatureVisitor2(api, loader, types, classSymbol));

            //new SignatureReader(signature).accept(new RootSignatureVisitor(api, loader, classSymbol));
        } else {
            if (superName != null) {
                final var superClassName = Type.getObjectType(superName).getClassName();
                var superTypeElement = loadClass(superClassName);

                if (superTypeElement == null) {
                    superTypeElement = createErrorSymbol(superClassName);
                }
                final var superType = superTypeElement.asType();
                classSymbol.setSuperType(superType);
            }

            for (final String anInterface : interfaces) {
                final var interfaceClassName = Type.getObjectType(anInterface).getClassName();
                var interfaceElement = loadClass(interfaceClassName);

                if (interfaceElement == null) {
                    interfaceElement = createErrorSymbol(interfaceClassName);
                }

                final var interfaceType = interfaceElement.asType();
                classSymbol.addInterface(interfaceType);
            }
        }
    }

    @Override
    public void visitInnerClass(final String name, final String outerName, final String innerName, final int access) {
        //name = java/util/Map$Entry
        //outerName=java/Util/Map
        //innerName=Entry
        final var innerClass = loadClass(name);

        if (innerClass != null) {
            final var enclosingClass = (ClassSymbol) innerClass.getEnclosingElement();

            if (enclosingClass != null) {
                enclosingClass.addEnclosedElement(innerClass);
            }
        }

        super.visitInnerClass(name, outerName, innerName, access);
    }

    private ErrorSymbol createErrorSymbol(final String className) {
        final var errorSymbol = ErrorSymbol.create();
        final var qualifiedName = QualifiedName.from(className);
        errorSymbol.setEnclosingElement(PackageSymbol.create(qualifiedName.packageName()));
        errorSymbol.setSimpleName(qualifiedName.simpleName());
        return errorSymbol;
    }

    private ElementKind getElementKind(final int access) {
        if (isKind(Opcodes.ACC_INTERFACE, access)) {
            return ElementKind.INTERFACE;
        } else if (isKind(Opcodes.ACC_ENUM, access)) {
            return ElementKind.ENUM;
        } else if (isKind(Opcodes.ACC_ANNOTATION, access)) {
            return ElementKind.ANNOTATION_TYPE;
        } else {
            return ElementKind.CLASS;
        }
    }

    private boolean isKind(final int opcode,
                           final int access) {
        return (access & opcode) == opcode;
    }

    private ClassSymbol loadClass(final String className) {
        return loader.loadTypeElement(className);
    }
}

