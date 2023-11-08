package io.github.potjerodekool.codegen.model.symbol;

import io.github.potjerodekool.codegen.model.element.NestingKind;

public abstract class AbstractClassBuilder<B extends AbstractClassBuilder<B, E>, E> extends AbstractSymbolBuilder<B> {

    protected NestingKind nestingKind = NestingKind.TOP_LEVEL;
    protected AbstractSymbol enclosingElement;

    public B nestingKind(final NestingKind nestingKind) {
        this.nestingKind = nestingKind;
        return (B) this;
    }

    public B enclosingElement(final AbstractSymbol enclosingElement) {
        this.enclosingElement = enclosingElement;
        return (B) this;
    }

    public abstract E build();
}
