package io.github.potjerodekool.codegen.model.tree.statement;

import io.github.potjerodekool.codegen.model.tree.TreeVisitor;
import io.github.potjerodekool.codegen.model.tree.expression.Expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BlockStatement extends AbstractStatement {

    private final List<Statement> statements = new ArrayList<>();

    public BlockStatement() {
        this(new ArrayList<>());
    }

    public BlockStatement(final Statement statement) {
        this(List.of(statement));
    }

    public BlockStatement(final Expression expression) {
        this(List.of(new ExpressionStatement(expression)));
    }

    public BlockStatement(final List<Statement> statements) {
        Objects.requireNonNull(statements);

        statements.forEach(Objects::requireNonNull);

        this.statements.addAll(statements);
    }

    public void add(final Statement statement) {
        this.statements.add(statement);
    }

    public void add(final Expression expression) {
        this.add(new ExpressionStatement(expression));
    }

    public List<Statement> getStatements() {
        return statements;
    }

    @Override
    public <R,P> R accept(final TreeVisitor<R, P> visitor,
                          final P param) {
        return visitor.visitBlockStatement(this, param);
    }
}
