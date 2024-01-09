package io.github.potjerodekool.codegen.template.model.expression;

import io.github.potjerodekool.codegen.model.tree.type.BoundKind;

public class WildCardTypeExpr implements TypeExpr {

    private BoundKind boundKind;
    private Expr expr;

    @Override
    public ExpressionKind getKind() {
        return ExpressionKind.WILDCARD;
    }

    @Override
    public <P, R> R accept(final ExpressionVisitor<P, R> visitor, final P p) {
        return null;
    }

    public BoundKind getBoundKind() {
        return boundKind;
    }

    public WildCardTypeExpr boundKind(final BoundKind boundKind) {
        this.boundKind = boundKind;
        return this;
    }

    public Expr getExpr() {
        return expr;
    }

    public WildCardTypeExpr expr(final Expr expr) {
        this.expr = expr;
        return this;
    }
}
