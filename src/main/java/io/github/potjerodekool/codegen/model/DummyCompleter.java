package io.github.potjerodekool.codegen.model;

import io.github.potjerodekool.codegen.model.symbol.AbstractSymbol;

public class DummyCompleter implements Completer {

    public static final Completer INSTANCE = new DummyCompleter();

    private DummyCompleter() {
    }

    @Override
    public boolean isComplete() {
        return true;
    }

    @Override
    public void complete(final AbstractSymbol symbol) {
    }

}
