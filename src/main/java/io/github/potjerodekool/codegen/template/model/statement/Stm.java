package io.github.potjerodekool.codegen.template.model.statement;

public interface Stm {

    StatementKind getKind();

    <P, R> R accept(StatementVisitor<P, R> visitor, P param);
}
