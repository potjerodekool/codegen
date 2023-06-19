package io.github.potjerodekool.codegen.model.element;

public enum NestingKind {

    TOP_LEVEL,

    MEMBER,

    LOCAL,

    ANONYMOUS;

    public boolean isNested() {
        return this != TOP_LEVEL;
    }
}
