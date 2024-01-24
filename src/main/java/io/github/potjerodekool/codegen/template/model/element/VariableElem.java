package io.github.potjerodekool.codegen.template.model.element;

import io.github.potjerodekool.codegen.template.model.expression.Expr;
import io.github.potjerodekool.codegen.template.model.type.TypeExpr;

public class VariableElem extends AbstractElem<VariableElem> {

    private TypeExpr type;

    private Expr initExpression;

    public TypeExpr getType() {
        return type;
    }

    public VariableElem type(final TypeExpr type) {
        this.type = type;
        return this;
    }

    public Expr getInitExpression() {
        return initExpression;
    }

    public VariableElem initExpression(final Expr initExpression) {
        this.initExpression = initExpression;
        return this;
    }

    @Override
    public <P, R> R accept(final ElementVisitor<P, R> visitor, final P p) {
        return visitor.visitVariableElement(this, p);
    }
}
