package io.github.potjerodekool.codegen.model;

import io.github.potjerodekool.codegen.model.symbol.AbstractSymbol;

public interface Completer {

    boolean isComplete();

    void complete(AbstractSymbol<?> symbol);
}
