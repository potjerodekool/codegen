package io.github.potjerodekool.codegen.template.model.element;

import io.github.potjerodekool.codegen.template.model.expression.TypeExpr;

public class VariableElem extends AbstractElem<VariableElem> {

    private TypeExpr type;

    public TypeExpr getType() {
        return type;
    }

    public VariableElem withType(final TypeExpr type) {
        this.type = type;
        return this;
    }


    @Override
    public <P, R> R accept(final ElementVisitor<P, R> visitor, final P p) {
        return visitor.visitVariableElement(this, p);
    }
}
