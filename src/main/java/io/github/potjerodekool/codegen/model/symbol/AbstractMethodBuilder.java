package io.github.potjerodekool.codegen.model.symbol;

import io.github.potjerodekool.codegen.model.type.TypeMirror;

public abstract class AbstractMethodBuilder<B extends AbstractMethodBuilder<B, E>, E> extends AbstractSymbolBuilder<B> {

    protected TypeMirror returnType;

    public B returnType(final TypeMirror returnType) {
        this.returnType = returnType;
        return (B) this;
    }

    public abstract E build();
}
