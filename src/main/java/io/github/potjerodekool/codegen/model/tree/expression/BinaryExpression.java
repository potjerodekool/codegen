package io.github.potjerodekool.codegen.model.tree.expression;

import io.github.potjerodekool.codegen.model.tree.TreeVisitor;

public class BinaryExpression extends AbstractExpression {

    private final Expression left;

    private final Expression right;

    private final Operator operator;

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

    public Expression getRight() {
        return right;
    }

    public Operator getOperator() {
        return operator;
    }

    @Override
    public <R, P> R accept(final TreeVisitor<R, P> visitor,
                           final P param) {
        return visitor.visitBinaryExpression(this, param);
    }
}
