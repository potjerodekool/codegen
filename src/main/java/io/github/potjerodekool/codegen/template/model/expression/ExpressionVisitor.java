package io.github.potjerodekool.codegen.template.model.expression;

public interface ExpressionVisitor<P, R> {

    R visitBinaryExpression(BinaryExpr binaryExpression, P p);

    R visitClassOrInterfaceTypeExpression(ClassOrInterfaceTypeExpr classOrInterfaceTypeExpression, P p);

    R visitIdentifierExpression(IdentifierExpr identifierExpression, P p);

    R visitMethodInvocationExpression(MethodInvocationExpr methodInvocationExpression, P p);

    R visitUnaryExpression(UnaryExpr unaryExpression, P p);

    <T> R visitLiteralExpression(LiteralExpr<?> tLiteralExpression, P p);

    R visitArrayExpression(ArrayExpr arrayExpression, P p);

    R visitPropertyAccessExpression(PropertyAccessExpr propertyAccessExpression, P p);

    R visitTypeExpression(SimpleTypeExpr simpleTypeExpr, P p);

    R visitFieldAccessExpression(FieldAccessExpr fieldAccessExpr, P p);
}
