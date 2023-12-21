package io.github.potjerodekool.codegen.template.model.expression;

import io.github.potjerodekool.codegen.model.tree.expression.Operator;

public class UnaryExpr implements Expr {

    private final Operator operator;

    private final Expr expression;

    private final boolean isPrefix;

    public UnaryExpr(final Operator operator,
                     final Expr expression) {
        this(operator, expression, true);
    }

    public UnaryExpr(final Operator operator,
                     final Expr expression,
                     final boolean isPrefix) {
        this.operator = operator;
        this.expression = expression;
        this.isPrefix = isPrefix;
    }

    public Operator getOperator() {
        return operator;
    }

    public Expr getExpression() {
        return expression;
    }

    public boolean isPrefix() {
        return isPrefix;
    }

    @Override
    public ExpressionKind getKind() {
        return ExpressionKind.UNARY;
    }

    @Override
    public <P, R> R accept(final ExpressionVisitor<P, R> visitor, final P p) {
        return visitor.visitUnaryExpression(this, p);
    }
}
