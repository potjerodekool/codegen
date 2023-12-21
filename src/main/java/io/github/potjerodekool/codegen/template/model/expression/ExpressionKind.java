package io.github.potjerodekool.codegen.template.model.expression;

public enum ExpressionKind {

    BINARY,
    CLASS_OR_INTERFACE,
    TYPE,
    IDENTIFIER,
    METHOD_INVOCATION,
    STRING_LITERAL,
    UNARY,
    ARRAY,
    PROPERTY_ACCESS,
    FIELD_ACCESS;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
