package io.github.potjerodekool.codegen.template.model.expression;

import io.github.potjerodekool.codegen.model.tree.expression.Operator;

public class BinaryExpr implements Expr {

    private Operator operator;
    private Expr left;
    private Expr right;

    public BinaryExpr() {
    }

    public BinaryExpr(final Operator operator,
                      final Expr left,
                      final Expr right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    public Operator getOperator() {
        return operator;
    }

    public BinaryExpr operator(Operator operator) {
        this.operator = operator;
        return this;
    }

    public Expr getLeft() {
        return left;
    }

    public BinaryExpr left(final Expr left) {
        this.left = left;
        return this;
    }

    public Expr getRight() {
        return right;
    }

    public BinaryExpr right(final Expr right) {
        this.right = right;
        return this;
    }

    @Override
    public ExpressionKind getKind() {
        return ExpressionKind.BINARY;
    }

    @Override
    public <P, R> R accept(final ExpressionVisitor<P, R> visitor, final P p) {
        return visitor.visitBinaryExpression(this, p);
    }
}
