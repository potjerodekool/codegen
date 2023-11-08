package io.github.potjerodekool.codegen.model.symbol;

import io.github.potjerodekool.codegen.model.element.Name;

public abstract class AbstractSymbolBuilder<B extends AbstractSymbolBuilder> {

    protected Name simpleName;

    public B simpleName(final CharSequence simpleName) {
        if (simpleName instanceof Name name) {
            this.simpleName = name;
        } else {
            this.simpleName = Name.of(simpleName);
        }
        return (B) this;
    }
}
