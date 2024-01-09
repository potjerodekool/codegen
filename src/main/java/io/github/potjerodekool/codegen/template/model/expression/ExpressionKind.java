package io.github.potjerodekool.codegen.template.model.expression;

public enum ExpressionKind {

    ANNOTATION,
    BINARY,
    CLASS_OR_INTERFACE,
    TYPE,
    WILDCARD,
    IDENTIFIER,
    METHOD_INVOCATION,
    LITERAL,
    NEW_CLASS,
    UNARY,
    ARRAY,
    PROPERTY_ACCESS,
    FIELD_ACCESS;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
