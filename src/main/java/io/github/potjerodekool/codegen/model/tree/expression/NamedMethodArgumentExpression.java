package io.github.potjerodekool.codegen.model.tree.expression;

import io.github.potjerodekool.codegen.model.tree.TreeVisitor;

public class NamedMethodArgumentExpression extends AbstractExpression {

    private final String name;

    private final Expression argument;

    public NamedMethodArgumentExpression(final String name,
                                         final Expression argument) {
        this.name = name;
        this.argument = argument;
    }

    public String getName() {
        return name;
    }

    public Expression getArgument() {
        return argument;
    }

    @Override
    public <R, P> R accept(final TreeVisitor<R, P> visitor, final P param) {
        return visitor.visitNamedMethodArgumentExpression(this, param);
    }
}
