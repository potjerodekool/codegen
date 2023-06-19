package io.github.potjerodekool.codegen.resolve;

import io.github.potjerodekool.codegen.loader.TypeElementLoader;
import io.github.potjerodekool.codegen.model.type.TypeMirror;

public class TypeResolver {

    private final TypeElementLoader typeElementLoader;

    public TypeResolver(final TypeElementLoader typeElementLoader) {
        this.typeElementLoader = typeElementLoader;
    }

    public TypeMirror resolveType(final String name) {
        final var symbol = typeElementLoader.loadTypeElement(name);

        if (symbol != null) {

        }

        throw new UnsupportedOperationException();
    }
}
