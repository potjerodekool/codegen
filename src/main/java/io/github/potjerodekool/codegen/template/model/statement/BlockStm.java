package io.github.potjerodekool.codegen.template.model.statement;

import java.util.ArrayList;
import java.util.List;

public class BlockStm implements Stm {

    private final List<Stm> statements = new ArrayList<>();

    public BlockStm() {
    }

    public BlockStm(final Stm statement) {
        this.statements.add(statement);
    }

    public BlockStm(final List<Stm> statements) {
        this.statements.addAll(statements);
    }

    public List<Stm> getStatements() {
        return statements;
    }

    public BlockStm statement(final Stm statement) {
        this.statements.add(statement);
        return this;
    }

    @Override
    public StatementKind getKind() {
        return StatementKind.BLOCK;
    }

    @Override
    public <P, R> R accept(final StatementVisitor<P, R> visitor, final P param) {
        return visitor.visitBlockStatement(this, param);
    }
}
