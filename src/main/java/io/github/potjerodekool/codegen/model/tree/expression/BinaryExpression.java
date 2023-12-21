package io.github.potjerodekool.codegen.model.tree.expression;

import io.github.potjerodekool.codegen.model.tree.TreeVisitor;

public class BinaryExpression extends AbstractExpression {

    private Expression left;

    private Expression right;

    private Operator operator;

    public BinaryExpression() {
    }

    public BinaryExpression(final Expression left,
                            final Expression right,
                            final Operator operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    public Expression getLeft() {
        return left;
    }

    public BinaryExpression left(final Expression left) {
        this.left = left;
        return this;
    }

    public Expression getRight() {
        return right;
    }

    public BinaryExpression right(final Expression right) {
        this.right = right;
        return this;
    }

    public Operator getOperator() {
        return operator;
    }

    public BinaryExpression operator(final Operator operator) {
        this.operator = operator;
        return this;
    }

    @Override
    public <R, P> R accept(final TreeVisitor<R, P> visitor,
                           final P param) {
        return visitor.visitBinaryExpression(this, param);
    }
}
