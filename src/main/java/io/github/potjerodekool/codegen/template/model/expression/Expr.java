package io.github.potjerodekool.codegen.template.model.expression;

public interface Expr {

    ExpressionKind getKind();

    <P, R> R accept(ExpressionVisitor<P, R> visitor, P p);
}
