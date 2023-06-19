package io.github.potjerodekool.codegen.model.tree.type;

import io.github.potjerodekool.codegen.model.tree.TreeVisitor;
import io.github.potjerodekool.codegen.model.tree.expression.AbstractExpression;
import io.github.potjerodekool.codegen.model.tree.expression.Expression;
import io.github.potjerodekool.codegen.model.type.TypeKind;

public class ArrayTypeExpression extends AbstractExpression implements TypeExpression {

    private final Expression componentTypeExpression;

    public ArrayTypeExpression(final Expression componentTypeExpression) {
        this.componentTypeExpression = componentTypeExpression;
    }

    public Expression getComponentTypeExpression() {
        return componentTypeExpression;
    }

    @Override
    public boolean isNullable() {
        return false;
    }

    @Override
    public TypeKind getKind() {
        return TypeKind.ARRAY;
    }

    @Override
    public <R, P> R accept(final TreeVisitor<R, P> visitor, final P param) {
        return visitor.visitArrayTypeExpresion(this, param);
    }
}
