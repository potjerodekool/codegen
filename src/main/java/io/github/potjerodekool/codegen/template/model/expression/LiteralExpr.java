package io.github.potjerodekool.codegen.template.model.expression;

public abstract class LiteralExpr implements Expr {

    @Override
    public ExpressionKind getKind() {
        return ExpressionKind.LITERAL;
    }

    public abstract Object getValue();

    @Override
    public <P, R> R accept(final ExpressionVisitor<P, R> visitor, final P p) {
        return visitor.visitLiteralExpression(this, p);
    }
}
