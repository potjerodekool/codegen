package io.github.potjerodekool.codegen.template.model.type;

import io.github.potjerodekool.codegen.template.model.expression.ExpressionKind;
import io.github.potjerodekool.codegen.template.model.expression.ExpressionVisitor;
import io.github.potjerodekool.codegen.template.model.type.TypeExpr;

public class SimpleTypeExpr implements TypeExpr {

    private final String name;

    public SimpleTypeExpr(final String name) {
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
        return visitor.visitTypeExpression(this, p);
    }
}
