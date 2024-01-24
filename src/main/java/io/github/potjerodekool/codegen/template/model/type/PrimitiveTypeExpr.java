package io.github.potjerodekool.codegen.template.model.type;

import io.github.potjerodekool.codegen.template.model.expression.ExpressionKind;
import io.github.potjerodekool.codegen.template.model.expression.ExpressionVisitor;

public class PrimitiveTypeExpr implements TypeExpr {

    private final String name;

    public PrimitiveTypeExpr(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public ExpressionKind getKind() {
        return ExpressionKind.TYPE;
    }

    @Override
    public <P, R> R accept(final ExpressionVisitor<P, R> visitor, final P p) {
        return visitor.visitPrimitiveTypeExpression(this, p );
    }
}
