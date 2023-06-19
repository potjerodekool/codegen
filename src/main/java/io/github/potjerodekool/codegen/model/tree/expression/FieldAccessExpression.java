package io.github.potjerodekool.codegen.model.tree.expression;

import io.github.potjerodekool.codegen.model.tree.TreeVisitor;

public class FieldAccessExpression extends AbstractExpression {

    private final Expression scope;

    private final Expression field;

    public FieldAccessExpression(final Expression scope,
                                 final Expression field) {
        this.scope = scope;
        this.field = field;
    }

    public FieldAccessExpression(final Expression scope,
                                 final String field) {
        this.scope = scope;
        this.field = new NameExpression(field);
    }

    public Expression getScope() {
        return scope;
    }

    public Expression getField() {
        return field;
    }

    @Override
    public <R, P> R accept(final TreeVisitor<R, P> visitor,
                           final P param) {
        return visitor.visitFieldAccessExpression(this, param);
    }
}
