package io.github.potjerodekool.codegen.model.tree.expression;

import io.github.potjerodekool.codegen.model.tree.TreeVisitor;

public class StringValueLiteralExpression extends AbstractExpression implements LiteralExpression {

    static final LiteralExpression NULL = new StringValueLiteralExpression("null", LiteralType.NULL);

    static final LiteralExpression TRUE = new StringValueLiteralExpression("true", LiteralType.BOOLEAN);
    static final LiteralExpression FALSE = new StringValueLiteralExpression("false", LiteralType.BOOLEAN);

    private final String value;

    private final LiteralType literalType;

    StringValueLiteralExpression(final String value, LiteralType literalType) {
        this.value = value;
        this.literalType = literalType;
    }

    public LiteralType getLiteralType() {
        return literalType;
    }

    public String getValue() {
        return value;
    }

    @Override
    public <R, P> R accept(final TreeVisitor<R, P> visitor,
                           final P param) {
        return visitor.visitLiteralExpression(this, param);
    }
}
