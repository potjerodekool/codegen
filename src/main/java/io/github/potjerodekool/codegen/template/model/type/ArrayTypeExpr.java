package io.github.potjerodekool.codegen.template.model.type;

import io.github.potjerodekool.codegen.template.model.expression.ExpressionKind;
import io.github.potjerodekool.codegen.template.model.expression.ExpressionVisitor;

public class ArrayTypeExpr implements TypeExpr {

    private final TypeExpr componentType;

    public ArrayTypeExpr(final TypeExpr componentType) {
        this.componentType = componentType;
    }

    public TypeExpr getComponentType() {
        return componentType;
    }

    @Override
    public ExpressionKind getKind() {
        return ExpressionKind.TYPE;
    }

    @Override
    public <P, R> R accept(final ExpressionVisitor<P, R> visitor, final P p) {
        return visitor.visitArrayTypeExpression(this, p);
    }
}
