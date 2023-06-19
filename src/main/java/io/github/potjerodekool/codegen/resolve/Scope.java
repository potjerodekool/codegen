package io.github.potjerodekool.codegen.resolve;

import io.github.potjerodekool.codegen.model.element.Name;
import io.github.potjerodekool.codegen.model.symbol.AbstractSymbol;

import java.util.Optional;

public interface Scope {

    GlobalScope findGlobalScope();

    Scope child();

    default void define(final AbstractSymbol<?> abstractSymbol) {
        define(abstractSymbol.getSimpleName(), abstractSymbol);
    }

    default void define(final String name,
                        final AbstractSymbol<?> abstractSymbol) {
        define(Name.of(name), abstractSymbol);
    }

    default void define(final Name name,
                        final AbstractSymbol<?> abstractSymbol) {
    }

    default Optional<AbstractSymbol<?>> resolveSymbol(final String name) {
        return resolveSymbol(Name.of(name));
    }

    default Optional<AbstractSymbol<?>> resolveSymbol(final Name name) {
        return Optional.empty();
    }
}
