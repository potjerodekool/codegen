package io.github.potjerodekool.codegen.model.type;

import io.github.potjerodekool.codegen.model.symbol.TypeVariableSymbol;
import org.checkerframework.checker.nullness.qual.Nullable;

public class TypeVariableImpl extends AbstractType implements TypeVariable {

    private final TypeVariableSymbol typeVariableSymbol;
    private AbstractType upperBound = null;
    private AbstractType lowerBound = null;

    public TypeVariableImpl(final TypeVariableSymbol typeVariableSymbol) {
        this.typeVariableSymbol = typeVariableSymbol;
    }

    @Override
    public <R, P> R accept(final TypeVisitor<R, P> visitor, final P param) {
        return visitor.visitTypeVariable(this, param);
    }

    @Override
    public <R, P> R accept(final Visitor<R, P> visitor, final P p) {
        return visitor.visitTypeVariable(this, p);
    }

    @Override
    public @Nullable TypeVariableSymbol asElement() {
        return typeVariableSymbol;
    }

    @Override
    public TypeMirror getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(final AbstractType upperBound) {
        this.upperBound = upperBound;
    }

    @Override
    public TypeMirror getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(final AbstractType lowerBound) {
        this.lowerBound = lowerBound;
    }

    @Override
    public TypeKind getKind() {
        return TypeKind.TYPEVAR;
    }

    @Override
    public String toString() {
        return typeVariableSymbol.getSimpleName().toString();
    }
}
