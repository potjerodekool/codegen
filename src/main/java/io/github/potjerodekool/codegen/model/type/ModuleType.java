package io.github.potjerodekool.codegen.model.type;

public class ModuleType extends AbstractType implements NoType {

    @Override
    public <R, P> R accept(final TypeVisitor<R, P> visitor,
                           final P param) {
        return visitor.visitNoType(this, param);
    }

    @Override
    public <R, P> R accept(final Visitor<R, P> visitor,
                           final P param) {
        return visitor.visitNoType(this, param);
    }

    @Override
    public TypeKind getKind() {
        return TypeKind.MODULE;
    }
}
