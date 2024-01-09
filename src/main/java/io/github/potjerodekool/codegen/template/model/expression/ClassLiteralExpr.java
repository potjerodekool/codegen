package io.github.potjerodekool.codegen.template.model.expression;

public class ClassLiteralExpr extends LiteralExpr {

    private final String className;

    public ClassLiteralExpr(final String className) {
        this.className = className;
    }

    @Override
    public Object getValue() {
        return className + ".class";
    }
}
