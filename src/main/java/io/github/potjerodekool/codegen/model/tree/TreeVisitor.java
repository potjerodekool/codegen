package io.github.potjerodekool.codegen.model.tree;

import io.github.potjerodekool.codegen.model.tree.expression.*;
import io.github.potjerodekool.codegen.model.tree.statement.*;
import io.github.potjerodekool.codegen.model.tree.type.*;

public interface TreeVisitor<R,P> {
    
    default void throwException() {
        throw new UnsupportedOperationException(getClass().getName());
    }

    default R visitTypeParameter(TypeParameter typeParameter, P param) {
        throwException();
        return null;
    }

    //expression

    default R visitUnknown(Expression expression,
                           P param) {
        throwException();
        return null;
    }

    default R visitBinaryExpression(BinaryExpression binaryExpression,
                                    P param) {
        throwException();
        return null;
    }

    default R visitIdentifierExpression(IdentifierExpression identifierExpression,
                                        P param) {
        throwException();
        return null;
    }

    default R visitFieldAccessExpression(FieldAccessExpression fieldAccessExpression,
                                         P param) {
        throwException();
        return null;
    }

    default R visitLiteralExpression(LiteralExpression literalExpression,
                                     P param) {
        throwException();
        return null;
    }

    default R visitMethodCall(MethodCallExpression methodCallExpression,
                              P param) {
        throwException();
        return null;
    }

    default R visitArrayInitializerExpression(ArrayInitializerExpression arrayInitializerExpression,
                                              P param) {
        throwException();
        return null;
    }

    default R visitNamedMethodArgumentExpression(NamedMethodArgumentExpression namedMethodArgumentExpression,
                                                 P param) {
        throwException();
        return null;
    }

    default R visitArrayAccessExpression(ArrayAccessExpression arrayAccessExpression,
                                         P param) {
        throwException();
        return null;
    }

    default R visitErrorExpression(final ErrorExpression errorExpression,
                                   final P param) {
        return visitUnknown(errorExpression, param);
    }

    default R visitNewClassExpression(NewClassExpression newClassExpression, P param) {
        return visitUnknown(newClassExpression, param);
    }

    default R visitAnnotatedType(AnnotatedTypeExpression annotatedTypeExpression, P param) {
        return visitUnknown(annotatedTypeExpression, param);
    }

    default R visitVarTypeExpression(VarTypeExpression varTypeExpression, P param) {
        return visitUnknown(varTypeExpression, param);
    }

    //statements

    default R visitUnknown(Statement statement,
                   P param) {
        throwException();
        return null;
    }

    default R visitBlockStatement(BlockStatement blockStatement,
                          P param) {
        throwException();
        return null;
    }

    default R visitExpressionStatement(ExpressionStatement expressionStatement,
                               P param) {
        throwException();
        return null;
    }

    default R visitReturnStatement(ReturnStatement returnStatement,
                           P param) {
        throwException();
        return null;
    }

    default R visitIfStatement(IfStatement ifStatement,
                       P param) {
        throwException();
        return null;
    }

    default R visitErrorStatement(final ErrorStatement errorStatement,
                                  final P param) {
        return visitUnknown(errorStatement, param);
    }

    default R visitPackageDeclaration(PackageDeclaration packageDeclaration, P param) {
        throwException();
        return null;
    }

    default R visitPrimitiveTypeExpression(PrimitiveTypeExpression primitiveTypeExpression, P param) {
        throwException();
        return null;
    }

    default R visitWildCardTypeExpression(WildCardTypeExpression wildCardTypeExpression, P param) {
        throwException();
        return null;
    }

    default R visitArrayTypeExpresion(ArrayTypeExpression arrayTypeExpression, P param) {
        throwException();
        return null;
    }

    default R visitNoType(NoTypeExpression noTypeExpression, P param) {
        throwException();
        return null;
    }

    default R visitAnnotationExpression(AnnotationExpression annotationExpression, P param) {
        throwException();
        return null;
    }

    default R visitClassOrInterfaceTypeExpression(ClassOrInterfaceTypeExpression classOrInterfaceTypeExpression, P param) {
        throwException();
        return null;
    }

    default R visitPropertyAccessExpression(PropertyAccessExpression propertyAccessExpression, P param) {
        throwException();
        return null;
    }

    default R visitUnaryExpression(UnaryExpression unaryExpression, P param) {
        throwException();
        return null;
    }

    default R visitClassDeclaration(final ClassDeclaration classDeclaration, final P param) {
        throwException();
        return null;
    }

    default R visitMethodDeclaration(final MethodDeclaration methodDeclaration, final P param) {
        throwException();
        return null;
    }

    default R visitVariableDeclaration(final VariableDeclaration variableDeclaration, final P param) {
        throwException();
        return null;
    }
}
