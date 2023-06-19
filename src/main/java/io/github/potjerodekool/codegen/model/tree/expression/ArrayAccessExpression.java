package io.github.potjerodekool.codegen.model.tree.expression;

import io.github.potjerodekool.codegen.model.tree.TreeVisitor;

public class ArrayAccessExpression extends AbstractExpression {

    private final Expression arrayExpression;

    private final Expression indexExpression;

    public ArrayAccessExpression(final Expression arrayExpression,
                                 final Expression indexExpression) {
        this.arrayExpression = arrayExpression;
        this.indexExpression = indexExpression;
    }

    public Expression getArrayExpression() {
        return arrayExpression;
    }

    public Expression getIndexExpression() {
        return indexExpression;
    }

    @Override
    public <R, P> R accept(final TreeVisitor<R, P> visitor, final P param) {
        return visitor.visitArrayAccessExpression(this, param);
    }
}
