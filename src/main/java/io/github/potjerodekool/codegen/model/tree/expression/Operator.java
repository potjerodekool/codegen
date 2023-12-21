package io.github.potjerodekool.codegen.model.tree.expression;

public enum Operator {

    EQUALS("=="),
    NOT_EQUALS("!="),
    ASSIGN("="),
    MINUS("-"),
    PLUS("+"),
    LESSER_THEN("<"),
    GREATER_THEN(">"),
    ADD("+="),
    NOT("!");

    private final String value;

    private Operator(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
