package io.github.potjerodekool.codegen.resolve;

import io.github.potjerodekool.codegen.model.element.Name;
import io.github.potjerodekool.codegen.model.symbol.AbstractSymbol;

import java.util.Optional;

public abstract class Scope {

    public final AbstractSymbol owner;

    protected Scope(final AbstractSymbol owner) {
        this.owner = owner;
    }

    public abstract Optional<AbstractSymbol> resolveSymbol(Name name);

    public abstract void define(AbstractSymbol symbol);
}
