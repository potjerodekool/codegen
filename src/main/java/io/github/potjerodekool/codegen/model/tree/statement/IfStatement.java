package io.github.potjerodekool.codegen.model.tree.statement;

import io.github.potjerodekool.codegen.model.tree.TreeVisitor;
import io.github.potjerodekool.codegen.model.tree.expression.Expression;

public class IfStatement extends AbstractStatement {

    private final Expression condition;

    private final BlockStatement body;

    public IfStatement(final Expression condition,
                       final BlockStatement body) {
        this.condition = condition;
        this.body = body;
    }

    public Expression getCondition() {
        return condition;
    }

    public BlockStatement getBody() {
        return body;
    }

    @Override
    public <R, P> R accept(final TreeVisitor<R, P> visitor, final P param) {
        return visitor.visitIfStatement(this, param);
    }
}
