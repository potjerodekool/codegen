package io.github.potjerodekool.codegen.loader;

import io.github.potjerodekool.codegen.model.symbol.ClassSymbol;

public interface TypeElementLoader {
    ClassSymbol loadTypeElement(String name);
}
