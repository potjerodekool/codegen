package io.github.potjerodekool.codegen.model.tree;

import io.github.potjerodekool.codegen.model.type.TypeMirror;

public class TypeParameter implements Tree {

    @Override
    public TypeMirror getType() {
        return null;
    }

    @Override
    public TypeParameter type(final TypeMirror type) {
        return this;
    }

    @Override
    public <R, P> R accept(final TreeVisitor<R, P> visitor, final P param) {
        throw new UnsupportedOperationException();
    }
}
