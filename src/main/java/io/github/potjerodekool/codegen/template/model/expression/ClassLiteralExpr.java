package io.github.potjerodekool.codegen.template.model.expression;

public class ClassLiteralExpr extends LiteralExpr {

    private String className;

    public ClassLiteralExpr(final String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public ClassLiteralExpr className(final String className) {
        this.className = className;
        return this;
    }

    @Override
    public boolean getIsNullLiteral() {
        return false;
    }

    @Override
    public Object getValue() {
        return className + ".class";
    }
}
