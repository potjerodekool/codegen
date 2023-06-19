package io.github.potjerodekool.codegen.model.type;

public final class NullTypeImpl extends AbstractType implements NullType {

    public static final NullTypeImpl INSTANCE = new NullTypeImpl();

    private NullTypeImpl() {
    }

    @Override
    public <R, P> R accept(final TypeVisitor<R, P> visitor, final P param) {
        return visitor.visitNull(this, param);
    }

    @Override
    public <R, P> R accept(final Visitor<R, P> visitor, final P p) {
        return visitor.visitNull(this, p);
    }

    @Override
    public TypeKind getKind() {
        return TypeKind.NULL;
    }

}
