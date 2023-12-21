package io.github.potjerodekool.codegen.template.model.statement;

import io.github.potjerodekool.codegen.template.model.expression.Expr;

public class IfStm implements Stm {

    private Expr condition;

    private BlockStm thenStatement;

    private Stm elseStatement;

    public Expr getCondition() {
        return condition;
    }

    public IfStm condition(final Expr condition) {
        this.condition = condition;
        return this;
    }

    public BlockStm getThenStatement() {
        return thenStatement;
    }

    public IfStm thenStatement(final BlockStm thenStatement) {
        this.thenStatement = thenStatement;
        return this;
    }

    public IfStm thenStatement(final Stm thenStatement) {
        if (thenStatement instanceof BlockStm blockStm) {
            this.thenStatement = blockStm;
        } else {
            this.thenStatement = new BlockStm(thenStatement);
        }
        return this;
    }

    public IfStm thenStatement(final Expr expression) {
        return thenStatement(new ExpressionStm(expression));
    }

    public Stm getElseStatement() {
        return elseStatement;
    }

    public IfStm elseStatement(final Stm elseStatement) {
        this.elseStatement = elseStatement;
        return this;
    }

    @Override
    public StatementKind getKind() {
        return StatementKind.IF;
    }

    @Override
    public <P, R> R accept(final StatementVisitor<P, R> visitor, final P param) {
        return visitor.visitIfStatement(this, param);
    }
}
