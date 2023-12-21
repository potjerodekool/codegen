package io.github.potjerodekool.codegen.template.model.statement;

import io.github.potjerodekool.codegen.template.model.expression.Expr;

public class ExpressionStm implements Stm {

    private final Expr expression;

    public ExpressionStm(final Expr expression) {
        this.expression = expression;
    }

    public Expr getExpression() {
        return expression;
    }

    @Override
    public StatementKind getKind() {
        return StatementKind.STATEMENT_EXPRESSION;
    }

    @Override
    public <P, R> R accept(final StatementVisitor<P, R> visitor, final P param) {
        return visitor.visitStatementExpression(this, param);
    }
}
