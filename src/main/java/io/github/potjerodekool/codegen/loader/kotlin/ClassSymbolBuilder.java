package io.github.potjerodekool.codegen.loader.kotlin;

import io.github.potjerodekool.codegen.model.element.ElementKind;
import io.github.potjerodekool.codegen.model.element.Name;
import io.github.potjerodekool.codegen.model.element.NestingKind;
import io.github.potjerodekool.codegen.model.symbol.ClassSymbol;
import io.github.potjerodekool.codegen.model.symbol.TypeVariableSymbol;
import io.github.potjerodekool.codegen.model.type.ClassType;
import io.github.potjerodekool.codegen.model.util.QualifiedName;
import io.github.potjerodekool.codegen.model.util.SymbolTable;
import kotlinx.metadata.internal.metadata.deserialization.Flags;
import org.jetbrains.kotlin.metadata.ProtoBuf;
import org.jetbrains.kotlin.metadata.deserialization.NameResolver;
import org.jetbrains.kotlin.serialization.deserialization.ClassData;

public class ClassSymbolBuilder {

    private final SymbolTable symbolTable;
    private final NameResolver nameResolver;

    private ClassSymbol classSymbol;
    private ClassType classType;

    public ClassSymbolBuilder(final SymbolTable symbolTable,
                              final NameResolver nameResolver) {
        this.symbolTable = symbolTable;
        this.nameResolver = nameResolver;
    }

    public ClassSymbol build(final ClassData classData) {
        visit(classData);
        return classSymbol;
    }

    private QualifiedName getQualifiedClassName(final int index) {
        return QualifiedName.from(nameResolver.getQualifiedClassName(index).replace('/', '.'));
    }

    private void visit(final ClassData classData) {
        final var classProto = classData.getClassProto();
        final var className = getQualifiedClassName(classProto.getFqName());

        final var packageSymbol = symbolTable.findOrCreatePackageSymbol(className.packageName());
        this.classSymbol = ClassSymbol.create(getElementKind(classProto), className.simpleName(), NestingKind.TOP_LEVEL, packageSymbol);
        symbolTable.addClass(classSymbol);

        this.classType = new ClassType(classSymbol, false);
        classSymbol.setType(classType);

        visitSuperTypes(classProto, classSymbol);
        visitTypeParameters(classProto, classSymbol);
    }

    private void visitSuperTypes(final ProtoBuf.Class classProto,
                                 final ClassSymbol classSymbol) {
        final var superTypeCount = classProto.getSupertypeCount();

        for (var superTypeIndex = 0; superTypeIndex < superTypeCount; superTypeIndex++) {
            final var superType = classProto.getSupertype(superTypeIndex);
            throw new UnsupportedOperationException();
        }
    }

    private void visitTypeParameters(final ProtoBuf.Class classProto,
                                     final ClassSymbol classSymbol) {
        final var typeParameters = classProto.getTypeParameterList();

        for (final ProtoBuf.TypeParameter typeParameter : typeParameters) {
            final var name = nameResolver.getString(typeParameter.getName());
            final var typeVariableSymbol = new TypeVariableSymbol(Name.of(name));
            classSymbol.addTypeParameter(typeVariableSymbol);
            classType.addTypeArgument(typeVariableSymbol.asType());
        }
    }

    private ElementKind getElementKind(final ProtoBuf.Class classProto) {
        final var classKind = Flags.CLASS_KIND.get(classProto.getFlags());
        return switch (classKind) {
            case INTERFACE -> ElementKind.INTERFACE;
            case ENUM_CLASS -> ElementKind.ENUM;
            case ANNOTATION_CLASS -> ElementKind.ANNOTATION_TYPE;
            default -> ElementKind.CLASS;
        };
    }
}
