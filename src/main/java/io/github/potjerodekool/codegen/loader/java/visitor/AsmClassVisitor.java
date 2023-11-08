package io.github.potjerodekool.codegen.loader.java.visitor;

import io.github.potjerodekool.codegen.loader.java.visitor.signature.TypeBuilder;
import io.github.potjerodekool.codegen.model.element.*;
import io.github.potjerodekool.codegen.model.symbol.ClassSymbol;
import io.github.potjerodekool.codegen.model.symbol.ErrorSymbol;
import io.github.potjerodekool.codegen.model.symbol.ModuleSymbol;
import io.github.potjerodekool.codegen.model.type.ClassType;
import io.github.potjerodekool.codegen.model.util.Elements;
import io.github.potjerodekool.codegen.model.util.QualifiedName;
import io.github.potjerodekool.codegen.model.util.SymbolTable;
import io.github.potjerodekool.codegen.model.util.type.Types;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ModuleVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;

public class AsmClassVisitor extends ClassVisitor {

    private final SymbolTable symbolTable;
    private final Elements elements;
    private final Types types;

    private ModuleSymbol moduleSymbol = ModuleSymbol.UNNAMED;
    private ClassSymbol classSymbol;

    public AsmClassVisitor(final int api,
                           final SymbolTable symbolTable,
                           final Elements elements,
                           final Types types) {
        super(api);
        this.symbolTable = symbolTable;
        this.elements = elements;
        this.types = types;
    }

    @Override
    public ModuleVisitor visitModule(final String name, final int access, final String version) {
        moduleSymbol = new ModuleSymbol(Name.of(name));
        return null;
    }

    @Override
    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
        final var elementKind = getElementKind(access);
        final var nestingKind = resolveNestingKind(name);

        final var qualifiedName = toClassName(name);
        final var fullName = Name.of(qualifiedName);

        if (nestingKind == NestingKind.TOP_LEVEL) {
            classSymbol = symbolTable.enterClass(moduleSymbol, fullName);
        } else {
            classSymbol = new ClassSymbol(
                    ElementKind.CLASS,
                    fullName.shortName(),
                    NestingKind.MEMBER,
                    null
            );
            symbolTable.addClass(moduleSymbol, classSymbol, fullName);
        }

        classSymbol.setKind(elementKind);
        classSymbol.setNestingKind(nestingKind);

        if (signature != null) {
            new SignatureReader(signature).accept(new TypeBuilder(api, elements, types, classSymbol));
        } else {
            if (superName != null) {
                final var superClassName = Type.getObjectType(superName).getClassName();
                var superTypeElement = elements.getTypeElement(moduleSymbol, superClassName);

                if (superTypeElement == null) {
                    superTypeElement = createErrorSymbol(superClassName);
                }
                final var superType = superTypeElement.asType();
                classSymbol.setSuperType(superType);
            }

            for (final String anInterface : interfaces) {
                final var interfaceClassName = Type.getObjectType(anInterface).getClassName();
                var interfaceElement = elements.getTypeElement(moduleSymbol, interfaceClassName);

                if (interfaceElement == null) {
                    interfaceElement = createErrorSymbol(interfaceClassName);
                }

                final var interfaceType = interfaceElement.asType();
                classSymbol.addInterface(interfaceType);
            }

            final var type = new ClassType(classSymbol, true);
            classSymbol.setType(type);
        }
    }

    private String toClassName(final String internalName) {
        return internalName.replace('/', '.').replace('$', '.');
    }

    @Override
    public void visitInnerClass(final String name, final String outerName, final String innerName, final int access) {
        if (outerName == null || innerName == null) {
            return;
        }

        final var innerClassname = outerName + "$" + innerName;

        final var innerClassSymbol = elements.getTypeElement(moduleSymbol, innerClassname);

        if (innerClassSymbol != null) {
            final var innerClass = (ClassSymbol) innerClassSymbol;
            innerClass.setEnclosingElement(classSymbol);
            classSymbol.addEnclosedElement(innerClassSymbol);
        }
    }

    public ClassSymbol getClassSymbol() {
        return classSymbol;
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

    private NestingKind resolveNestingKind(final String name) {
        //TODO resolve all kinds
        if (name.indexOf('$') > -1) {
            return NestingKind.MEMBER;
        } else {
            return NestingKind.TOP_LEVEL;
        }
    }

    private ErrorSymbol createErrorSymbol(final String className) {
        final var errorSymbol = ErrorSymbol.create();
        final var qualifiedName = QualifiedName.from(className);
        errorSymbol.setEnclosingElement(symbolTable.enterPackage(null, qualifiedName.packageName()));
        errorSymbol.setSimpleName(qualifiedName.simpleName());
        return errorSymbol;
    }
}
