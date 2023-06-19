package io.github.potjerodekool.codegen.model.tree.expression;

import io.github.potjerodekool.codegen.model.symbol.AbstractSymbol;
import io.github.potjerodekool.codegen.model.tree.TreeVisitor;

public class NameExpression extends AbstractExpression {

    private final String name;

    private AbstractSymbol<?> symbol;

    public NameExpression(final String name) {
        this(name, null);
    }

    public NameExpression(final String name,
                          final AbstractSymbol<?> symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public AbstractSymbol<?> getSymbol() {
        return symbol;
    }

    public void setSymbol(final AbstractSymbol<?> symbol) {
        this.symbol = symbol;
    }

    @Override
    public <R, P> R accept(final TreeVisitor<R, P> visitor,
                           final P param) {
        return visitor.visitNameExpression(this, param);
    }
}
