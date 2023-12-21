package io.github.potjerodekool.codegen.template.model.statement;

public interface StatementVisitor<P, R> {

    R visitBlockStatement(BlockStm blockStatement, P param);

    R visitIfStatement(IfStm ifStatement, P param);

    R visitReturnStatement(ReturnStm returnStatement, P param);

    R visitStatementExpression(ExpressionStm statementExpression, P param);

    R visitVariableDeclarationStatement(VariableDeclarationStm variableDeclarationStatement, P param);
}
