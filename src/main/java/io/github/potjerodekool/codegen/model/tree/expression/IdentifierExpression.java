package io.github.potjerodekool.codegen.model.tree.expression;

import io.github.potjerodekool.codegen.model.element.Element;
import io.github.potjerodekool.codegen.model.tree.TreeVisitor;

public class IdentifierExpression extends AbstractExpression {

    private final String name;

    private Element symbol;

    public IdentifierExpression(final String name) {
        this(name, null);
    }

    public IdentifierExpression(final String name,
                                final Element symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public Element getSymbol() {
        return symbol;
    }

    public void setSymbol(final Element symbol) {
        this.symbol = symbol;
    }

    @Override
    public <R, P> R accept(final TreeVisitor<R, P> visitor,
                           final P param) {
        return visitor.visitIdentifierExpression(this, param);
    }
}
