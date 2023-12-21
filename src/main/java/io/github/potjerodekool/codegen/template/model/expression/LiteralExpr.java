package io.github.potjerodekool.codegen.template.model.expression;

public interface LiteralExpr<T> extends Expr {

    T getValue();

    @Override
    default <P, R> R accept(ExpressionVisitor<P, R> visitor, P p) {
        return visitor.visitLiteralExpression(this, p);
    }
}
