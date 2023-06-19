package io.github.potjerodekool.codegen.model.tree.expression;

import io.github.potjerodekool.codegen.model.tree.TreeVisitor;

public class ErrorExpression extends AbstractExpression {

    @Override
    public <R, P> R accept(final TreeVisitor<R, P> visitor, final P param) {
        return visitor.visitErrorExpression(this, param);
    }
}
