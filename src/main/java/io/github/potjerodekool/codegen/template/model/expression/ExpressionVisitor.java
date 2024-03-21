package io.github.potjerodekool.codegen.template.model.expression;

import io.github.potjerodekool.codegen.template.model.annotation.Annot;
import io.github.potjerodekool.codegen.template.model.type.*;

public interface ExpressionVisitor<P, R> {

    R visitBinaryExpression(BinaryExpr binaryExpression, P p);

    R visitClassOrInterfaceTypeExpression(ClassOrInterfaceTypeExpr classOrInterfaceTypeExpression, P p);

    R visitIdentifierExpression(IdentifierExpr identifierExpression, P p);

    R visitMethodInvocationExpression(MethodInvocationExpr methodInvocationExpression, P p);

    R visitUnaryExpression(UnaryExpr unaryExpression, P p);

    R visitLiteralExpression(LiteralExpr tLiteralExpression, P p);

    R visitArrayExpression(ArrayExpr arrayExpression, P p);

    R visitPropertyAccessExpression(PropertyAccessExpr propertyAccessExpression, P p);

    R visitFieldAccessExpression(FieldAccessExpr fieldAccessExpr, P p);

    R visitNewClassExpression(NewClassExpr newClassExpr, P p);

    R visitPrimitiveTypeExpression(PrimitiveTypeExpr primitiveTypeExpr, P p);

    R visitArrayTypeExpression(ArrayTypeExpr arrayTypeExpr, P p);

    R visitNoTypeExpression(NoTypeExpr noTypeExpr, P p);

    R visitVarTypeExpression(VarTypeExp varTypeExp, P p);

    R visitAnnotation(Annot annot, P param);

    R visitTypeVarExpression(TypeVarExpr typeVarExpr, P p);
}
