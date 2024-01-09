package io.github.potjerodekool.codegen.model.tree.statement;

import io.github.potjerodekool.codegen.model.type.TypeMirror;

public abstract class AbstractStatement implements Statement {

    @Override
    public TypeMirror getType() {
        return null;
    }

    @Override
    public AbstractStatement type(final TypeMirror type) {
        return this;
    }
}
