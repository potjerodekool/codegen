package io.github.potjerodekool.codegen.template.model.statement;

import io.github.potjerodekool.codegen.template.model.expression.Expr;

public class ReturnStm implements Stm {

    private final Expr expression;

    public ReturnStm(final Expr expression) {
        this.expression = expression;
    }

    public Expr getExpression() {
        return expression;
    }

    @Override
    public StatementKind getKind() {
        return StatementKind.RETURN;
    }

    @Override
    public <P, R> R accept(final StatementVisitor<P, R> visitor, final P param) {
        return visitor.visitReturnStatement(this, param);
    }
}
