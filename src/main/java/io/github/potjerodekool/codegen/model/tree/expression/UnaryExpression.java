package io.github.potjerodekool.codegen.model.tree.expression;

import io.github.potjerodekool.codegen.model.tree.TreeVisitor;

public class UnaryExpression extends AbstractExpression {

    private boolean isPrefix;

    private Operator operator;

    private Expression expression;

    public UnaryExpression() {
        isPrefix = true;
    }

    public boolean isPrefix() {
        return isPrefix;
    }

    public UnaryExpression prefix(final boolean isPrefix) {
        this.isPrefix = isPrefix;
        return this;
    }

    public Operator getOperator() {
        return operator;
    }

    public UnaryExpression operator(final Operator operator) {
        this.operator = operator;
        return this;
    }

    public Expression getExpression() {
        return expression;
    }

    public UnaryExpression expression(final Expression expression) {
        this.expression = expression;
        return this;
    }

    @Override
    public <R, P> R accept(final TreeVisitor<R, P> visitor, final P param) {
        return visitor.visitUnaryExpression(this, param);
    }
}
