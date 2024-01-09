package io.github.potjerodekool.codegen.model;

import io.github.potjerodekool.codegen.model.element.Name;
import io.github.potjerodekool.codegen.model.symbol.AbstractSymbol;
import io.github.potjerodekool.codegen.resolve.Scope;

import java.util.Optional;

public class CompositeScope extends Scope {
    public CompositeScope() {
        super(null, null);
    }

    @Override
    public Optional<AbstractSymbol> resolveSymbol(final Name name) {
        return Optional.empty();
    }

    @Override
    public void define(final AbstractSymbol symbol) {

    }
}
