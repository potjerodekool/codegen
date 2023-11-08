package io.github.potjerodekool.codegen.model.tree;

import io.github.potjerodekool.codegen.model.tree.java.JMethodDeclaration;
import io.github.potjerodekool.codegen.model.tree.statement.java.JClassDeclaration;
import io.github.potjerodekool.codegen.model.tree.statement.java.JVariableDeclaration;

public interface JTreeVisitor<R,P> extends TreeVisitor<R,P> {

    default R visitClassDeclaration(final JClassDeclaration classDeclaration, final P param) {
        throwException();
        return null;
    }

    default R visitMethodDeclaration(final JMethodDeclaration methodDeclaration, final P param) {
        throwException();
        return null;
    }

    default R visitVariableDeclaration(final JVariableDeclaration variableDeclaration, final P param) {
        throwException();
        return null;
    }
}
