package io.github.potjerodekool.codegen.template.model.element;

import io.github.potjerodekool.codegen.template.model.type.TypeExpr;
import io.github.potjerodekool.codegen.template.model.statement.BlockStm;

import java.util.ArrayList;
import java.util.List;

public class MethodElem extends AbstractElem<MethodElem> {

    private BlockStm body;

    private TypeExpr returnType;

    private final List<VariableElem> parameters = new ArrayList<>();

    public BlockStm getBody() {
        return body;
    }

    public void body(final BlockStm body) {
        this.body = body;
    }

    public List<VariableElem> getParameters() {
        return parameters;
    }

    public MethodElem parameter(final VariableElem parameter) {
        this.parameters.add(parameter);
        return this;
    }

    public TypeExpr getReturnType() {
        return returnType;
    }

    public MethodElem returnType(final TypeExpr returnType) {
        this.returnType = returnType;
        return this;
    }

    @Override
    public <P, R> R accept(final ElementVisitor<P, R> visitor, final P p) {
        return visitor.visitExecutableElement(this, p);
    }
}
