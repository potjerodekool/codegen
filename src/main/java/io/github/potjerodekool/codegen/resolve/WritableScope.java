package io.github.potjerodekool.codegen.resolve;

import io.github.potjerodekool.codegen.model.element.Name;
import io.github.potjerodekool.codegen.model.symbol.AbstractSymbol;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class WritableScope extends Scope {

    private final Map<Name, AbstractSymbol> symbols = new HashMap<>();

    public WritableScope(final AbstractSymbol owner,
                         final Scope parent) {
        super(owner, parent);
    }

    @Override
    public Optional<AbstractSymbol> resolveSymbol(final Name name) {
        final var resolvedSymbol = symbols.get(name);

        if (resolvedSymbol != null) {
            return Optional.of(resolvedSymbol);
        }

        final var resolved = owner.getEnclosedElements().stream()
                .filter(child -> child.getSimpleName().contentEquals(name))
                .map(child -> (AbstractSymbol) child)
                .findFirst();

        if (resolved.isPresent()) {
            return resolved;
        } else {
            return resolveInParent(name);
        }
    }

    private Optional<AbstractSymbol> resolveInParent(final Name name) {
        final var enclosed = (AbstractSymbol) owner.getEnclosingElement();

        if (enclosed == null) {
            return Optional.empty();
        }

        final var members = enclosed.members();

        if (members == null) {
            return Optional.empty();
        }

        return members.resolveSymbol(name);
    }

    @Override
    public void define(final AbstractSymbol symbol) {
        symbols.put(symbol.getSimpleName(), symbol);
    }
}
