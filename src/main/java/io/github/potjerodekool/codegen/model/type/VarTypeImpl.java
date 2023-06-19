package io.github.potjerodekool.codegen.model.type;

public class VarTypeImpl extends AbstractType implements VarType {

    private TypeMirror interferedType;

    public TypeMirror getInterferedType() {
        return interferedType;
    }

    public void setInterferedType(final TypeMirror interferedType) {
        this.interferedType = interferedType;
    }

    @Override
    public TypeKind getKind() {
        if (interferedType == null) {
            return TypeKind.OTHER;
        }
        return interferedType.getKind();
    }

    @Override
    public <R, P> R accept(final TypeVisitor<R, P> v, final P p) {
        return v.visitVarType(this, p);
    }


    @Override
    public <R, P> R accept(final Visitor<R, P> visitor, final P p) {
        return visitor.visitVarType(this, p);
    }

    @Override
    public boolean isVarType() {
        return true;
    }
}
