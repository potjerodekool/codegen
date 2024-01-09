package io.github.potjerodekool.codegen.template.model.expression;

public class PropertyAccessExpr implements Expr {

    private Expr target;

    private String name;

    public Expr getTarget() {
        return target;
    }

    public PropertyAccessExpr target(final Expr target) {
        this.target = target;
        return this;
    }

    public String getName() {
        return name;
    }

    public PropertyAccessExpr name(final String name) {
        this.name = name;
        return this;
    }

    @Override
    public ExpressionKind getKind() {
        return ExpressionKind.PROPERTY_ACCESS;
    }

    @Override
    public <P, R> R accept(final ExpressionVisitor<P, R> visitor, final P p) {
        return visitor.visitPropertyAccessExpression(this, p);
    }
}
