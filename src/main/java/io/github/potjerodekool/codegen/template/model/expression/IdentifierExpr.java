package io.github.potjerodekool.codegen.template.model.expression;

public class IdentifierExpr implements Expr {
    private final String name;

    public IdentifierExpr(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public ExpressionKind getKind() {
        return ExpressionKind.IDENTIFIER;
    }

    @Override
    public <P, R> R accept(final ExpressionVisitor<P, R> visitor, final P p) {
        return visitor.visitIdentifierExpression(this, p);
    }
}
