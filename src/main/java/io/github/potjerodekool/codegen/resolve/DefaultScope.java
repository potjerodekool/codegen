package io.github.potjerodekool.codegen.resolve;

import io.github.potjerodekool.codegen.model.element.Name;
import io.github.potjerodekool.codegen.model.symbol.AbstractSymbol;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DefaultScope implements Scope {

    private final Scope parent;

    private final Map<Name, AbstractSymbol<?>> symbols = new HashMap<>();


    public DefaultScope(final Scope parent) {
        this.parent = parent;
    }

    @Override
    public GlobalScope findGlobalScope() {
        return parent.findGlobalScope();
    }

    @Override
    public DefaultScope child() {
        return new DefaultScope(this);
    }

    @Override
    public void define(final Name name,
                       final AbstractSymbol<?> abstractSymbol) {
        symbols.put(name, abstractSymbol);
    }

    @Override
    public Optional<AbstractSymbol<?>> resolveSymbol(final Name name) {
        final var symbol = symbols.get(name);

        if (symbol != null) {
            return Optional.of(symbol);
        } else if (parent != null) {
            return parent.resolveSymbol(name);
        } else {
            return Optional.empty();
        }
    }
}
