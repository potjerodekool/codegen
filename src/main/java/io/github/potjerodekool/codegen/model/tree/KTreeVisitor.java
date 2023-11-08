package io.github.potjerodekool.codegen.model.tree;

import io.github.potjerodekool.codegen.model.tree.kotlin.KMethodDeclaration;
import io.github.potjerodekool.codegen.model.tree.statement.kotlin.KClassDeclaration;
import io.github.potjerodekool.codegen.model.tree.statement.kotlin.KVariableDeclaration;

public interface KTreeVisitor <R,P> extends TreeVisitor<R,P> {

    default R visitClassDeclaration(final KClassDeclaration classDeclaration, final P param) {
        throwException();
        return null;
    }

    default R visitMethodDeclaration(final KMethodDeclaration methodDeclaration, final P param) {
        throwException();
        return null;
    }

    default R visitVariableDeclaration(final KVariableDeclaration variableDeclaration, final P param) {
        throwException();
        return null;
    }
}

