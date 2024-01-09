package io.github.potjerodekool.codegen.template.model.expression;

public class SimpleLiteralExpr extends LiteralExpr {

    private final Object value;

    public SimpleLiteralExpr(final Object value) {
        this.value = value;
    }

    public Object getValue() {
        if (value instanceof String) {
            return "\"" + value + "\"";
        } else if (value instanceof Character c) {
            return '\'' + c + '\'';
        } else {
            return value;
        }
    }
}
