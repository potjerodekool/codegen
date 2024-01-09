package io.github.potjerodekool.codegen.template.model.expression;

public class FieldAccessExpr implements Expr {

    private Expr target;

    private Expr field;

    @Override
    public ExpressionKind getKind() {
        return ExpressionKind.FIELD_ACCESS;
    }

    public Expr getTarget() {
        return target;
    }

    public FieldAccessExpr target(final Expr target) {
        this.target = target;
        return this;
    }

    public Expr getField() {
        return field;
    }

    public FieldAccessExpr field(final Expr field) {
        this.field = field;
        return this;
    }

    @Override
    public <P, R> R accept(final ExpressionVisitor<P, R> visitor, final P p) {
        return visitor.visitFieldAccessExpression(this, p);
    }
}
