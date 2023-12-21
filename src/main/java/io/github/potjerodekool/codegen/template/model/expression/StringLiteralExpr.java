package io.github.potjerodekool.codegen.template.model.expression;

public class StringLiteralExpr implements LiteralExpr<String> {

    private final String value;

    public StringLiteralExpr(final String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public ExpressionKind getKind() {
        return ExpressionKind.STRING_LITERAL;
    }

    @Override
    public String toString() {
        return "\"" + value + "\"";
    }
}
