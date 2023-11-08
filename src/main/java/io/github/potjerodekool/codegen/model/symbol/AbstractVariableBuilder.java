package io.github.potjerodekool.codegen.model.symbol;

public abstract class AbstractVariableBuilder<B extends AbstractVariableBuilder<B, E>, E> extends AbstractSymbolBuilder<B> {

    public abstract E build();
}
