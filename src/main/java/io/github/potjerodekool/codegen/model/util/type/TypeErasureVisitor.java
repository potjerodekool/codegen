package io.github.potjerodekool.codegen.model.util.type;

import io.github.potjerodekool.codegen.model.symbol.ClassSymbol;
import io.github.potjerodekool.codegen.model.type.*;

import java.util.List;

public class TypeErasureVisitor extends AbstractTypeVisitor<TypeMirror, Object> {

    @Override
    public TypeMirror visitType(final TypeMirror type,
                                final Object o) {
        return type;
    }

    @Override
    public TypeMirror visitNoType(final NoType noType,
                                  final Object o) {
        return noType;
    }

    @Override
    public TypeMirror visitPrimitive(final PrimitiveType primitiveType,
                                     final Object o) {
        return primitiveType;
    }

    @Override
    public TypeMirror visitArray(final ArrayType arrayType,
                                 final Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TypeMirror visitDeclared(final DeclaredType declaredType,
                                    final Object o) {
        return new ClassType(
                (ClassSymbol) declaredType.asElement(),
                declaredType.getAnnotationMirrors(),
                List.of(),
                declaredType.isNullable()
        );
    }

    @Override
    public TypeMirror visitError(final ErrorTypeImpl errorType,
                                 final Object o) {
        final var declaredType = (DeclaredType) visitDeclared(errorType, o);
        return new ErrorTypeImpl(
                (ClassSymbol) declaredType.asElement(),
                declaredType.isNullable()
        );
    }

    @Override
    public TypeMirror visitNull(final NullType nullType,
                                final Object o) {
        return nullType;
    }

    @Override
    public TypeMirror visitWildcard(final WildcardType wildcardType,
                                    final Object param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TypeMirror visitExecutable(final ExecutableType executableType,
                                      final Object param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TypeMirror visitTypeVariable(final TypeVariable typeVariable, final Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TypeMirror visitVarType(final VarTypeImpl varType, final Object o) {
        throw new UnsupportedOperationException();
    }
}
