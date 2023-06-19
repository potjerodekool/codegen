package io.github.potjerodekool.codegen.model.tree.type;

import io.github.potjerodekool.codegen.model.tree.TreeVisitor;
import io.github.potjerodekool.codegen.model.tree.expression.AbstractExpression;
import io.github.potjerodekool.codegen.model.type.TypeKind;

public class WildCardTypeExpression extends AbstractExpression implements TypeExpression {

    private final BoundKind boundKind;

    private final TypeExpression typeExpression;

    public WildCardTypeExpression(final BoundKind boundKind, final TypeExpression typeExpression) {
        this.boundKind = boundKind;
        this.typeExpression = typeExpression;
    }

    public BoundKind getBoundKind() {
        return boundKind;
    }

    public TypeExpression getTypeExpression() {
        return typeExpression;
    }

    @Override
    public boolean isNullable() {
        return false;
    }

    @Override
    public TypeKind getKind() {
        return null;
    }

    @Override
    public <R, P> R accept(final TreeVisitor<R, P> visitor, final P param) {
        return visitor.visitWildCardTypeExpression(this, param);
    }

}
