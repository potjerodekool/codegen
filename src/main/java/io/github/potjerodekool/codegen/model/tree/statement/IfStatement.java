package io.github.potjerodekool.codegen.model.tree.statement;

import io.github.potjerodekool.codegen.model.tree.TreeVisitor;
import io.github.potjerodekool.codegen.model.tree.expression.Expression;

public class IfStatement extends AbstractStatement {

    private Expression condition;

    private BlockStatement body;

    public IfStatement() {
    }

    public IfStatement(final Expression condition,
                       final BlockStatement body) {
        this.condition = condition;
        this.body = body;
    }

    public Expression getCondition() {
        return condition;
    }

    public IfStatement condition(final Expression condition) {
        this.condition = condition;
        return this;
    }

    public BlockStatement getBody() {
        return body;
    }

    public IfStatement body(final BlockStatement body) {
        this.body = body;
        return this;
    }

    @Override
    public <R, P> R accept(final TreeVisitor<R, P> visitor, final P param) {
        return visitor.visitIfStatement(this, param);
    }
}
