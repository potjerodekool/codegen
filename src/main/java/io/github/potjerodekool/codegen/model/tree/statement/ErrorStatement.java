package io.github.potjerodekool.codegen.model.tree.statement;

import io.github.potjerodekool.codegen.model.tree.TreeVisitor;

public class ErrorStatement extends AbstractStatement {

    @Override
    public <R, P> R accept(final TreeVisitor<R, P> visitor, final P param) {
        return visitor.visitErrorStatement(this, param);
    }
}
