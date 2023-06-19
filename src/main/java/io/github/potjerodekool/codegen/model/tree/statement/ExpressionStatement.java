package io.github.potjerodekool.codegen.model.tree.statement;

import io.github.potjerodekool.codegen.model.tree.TreeVisitor;
import io.github.potjerodekool.codegen.model.tree.expression.Expression;

public class ExpressionStatement extends AbstractStatement {

    private final Expression expression;

    public ExpressionStatement(final Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public <R,P> R accept(final TreeVisitor<R, P> visitor,
                          final P param) {
        return visitor.visitExpressionStatement(this, param);
    }
}
