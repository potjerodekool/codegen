package io.github.potjerodekool.codegen.model.symbol;

import io.github.potjerodekool.codegen.model.element.ElementKind;
import io.github.potjerodekool.codegen.model.element.ElementVisitor;
import io.github.potjerodekool.codegen.model.element.VariableElement;

public class VariableSymbol extends AbstractSymbol implements VariableElement {

    public VariableSymbol(final ElementKind kind,
                             final CharSequence simpleName) {
        super(kind, simpleName);
    }

    @Override
    public <R, P> R accept(final ElementVisitor<R, P> v, final P p) {
        return v.visitVariable(this, p);
    }

    @Override
    public Object getConstantValue() {
        return null;
    }

}