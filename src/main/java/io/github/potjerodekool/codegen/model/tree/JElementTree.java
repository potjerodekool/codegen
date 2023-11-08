package io.github.potjerodekool.codegen.model.tree;

import io.github.potjerodekool.codegen.model.element.ElementKind;

public interface JElementTree extends Tree {

    ElementKind getKind();
}
