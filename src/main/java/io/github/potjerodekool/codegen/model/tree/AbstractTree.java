package io.github.potjerodekool.codegen.model.tree;

import io.github.potjerodekool.codegen.model.type.TypeMirror;

public abstract class AbstractTree implements Tree {

    private TypeMirror type;

    @Override
    public TypeMirror getType() {
        return type;
    }

    @Override
    public void setType(final TypeMirror type) {
        this.type = type;
    }

}
