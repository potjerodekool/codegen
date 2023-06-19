package io.github.potjerodekool.codegen.model.type.java;

import io.github.potjerodekool.codegen.model.type.AbstractType;
import io.github.potjerodekool.codegen.model.type.NoType;
import io.github.potjerodekool.codegen.model.type.TypeKind;
import io.github.potjerodekool.codegen.model.type.TypeVisitor;

public class JavaNoneType extends AbstractType implements NoType {

    public static final JavaNoneType INSTANCE = new JavaNoneType();

    @Override
    public TypeKind getKind() {
        return TypeKind.NONE;
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

