package io.github.potjerodekool.codegen.model.util.type.check;

import io.github.potjerodekool.codegen.model.type.*;
import io.github.potjerodekool.codegen.model.type.immutable.WildcardType;
import io.github.potjerodekool.codegen.model.util.type.SimpleVisitor;

abstract class AbstractTypeRelation extends SimpleVisitor<Boolean, TypeMirror> {

    AbstractTypeRelation() {
    }

    @Override
    public Boolean visitType(final TypeMirror type, final TypeMirror type2) {
        return false;
    }

    @Override
    public Boolean visitNoType(final NoType noType, final TypeMirror type) {
        return false;
    }

    @Override
    public Boolean visitPrimitive(final PrimitiveType primitiveType, final TypeMirror type) {
        return false;
    }

    @Override
    public Boolean visitArray(final ArrayType arrayType, final TypeMirror type) {
        return false;
    }

    @Override
    public Boolean visitDeclared(final DeclaredType declaredType, final TypeMirror type) {
        return false;
    }

    @Override
    public Boolean visitError(final ErrorTypeImpl errorType, final TypeMirror type) {
        return false;
    }

    @Override
    public Boolean visitNull(final NullType nullType, final TypeMirror type) {
        return false;
    }

    @Override
    public Boolean visitWildcard(final WildcardType wildcardType, final TypeMirror param) {
        return false;
    }

    @Override
    public Boolean visitExecutable(final ExecutableType executableType, final TypeMirror param) {
        return false;
    }

    @Override
    public Boolean visitTypeVariable(final TypeVariable typeVariable, final TypeMirror typeMirror) {
        return false;
    }

    @Override
    public Boolean visitVarType(final VarType varType, final TypeMirror typeMirror) {
        return false;
    }
}
