package io.github.potjerodekool.codegen.model.type.java;

import io.github.potjerodekool.codegen.model.type.*;

public class JavaVoidType extends AbstractType implements NoType {

    public static final JavaVoidType INSTANCE = new JavaVoidType();

    private JavaVoidType() {
    }

    @Override
    public TypeKind getKind() {
        return TypeKind.VOID;
    }

    @Override
    public <R, P> R accept(final TypeVisitor<R, P> v, final P p) {
        return v.visitNoType(this, p);
    }

    @Override
    public <R, P> R accept(final Visitor<R, P> visitor, final P p) {
        return visitor.visitNoType(this, p);
    }
}
