package io.github.potjerodekool.codegen.template;

import io.github.potjerodekool.codegen.model.CompilationUnit;
import io.github.potjerodekool.codegen.model.element.ElementKind;
import io.github.potjerodekool.codegen.model.tree.AnnotationExpression;
import io.github.potjerodekool.codegen.model.tree.MethodDeclaration;
import io.github.potjerodekool.codegen.model.tree.TreeVisitor;
import io.github.potjerodekool.codegen.model.tree.expression.*;
import io.github.potjerodekool.codegen.model.tree.statement.*;
import io.github.potjerodekool.codegen.model.tree.type.ClassOrInterfaceTypeExpression;
import io.github.potjerodekool.codegen.model.tree.type.NoTypeExpression;
import io.github.potjerodekool.codegen.template.model.annotation.Annot;
import io.github.potjerodekool.codegen.template.model.element.Elem;
import io.github.potjerodekool.codegen.template.model.element.MethodElem;
import io.github.potjerodekool.codegen.template.model.element.TypeElem;
import io.github.potjerodekool.codegen.template.model.element.VariableElem;
import io.github.potjerodekool.codegen.template.model.expression.*;
import io.github.potjerodekool.codegen.template.model.statement.*;

public class AstToTemplateModelTransformer implements TreeVisitor<Object, Object> {
    public io.github.potjerodekool.codegen.template.model.CompilationUnit transform(final CompilationUnit cu) {
        final var unit = new io.github.potjerodekool.codegen.template.model.CompilationUnit(cu.getLanguage());
        unit.withPackageName(cu.getPackageDeclaration().getName().getName());

        cu.getClassDeclarations().stream()
                .map(classDeclaration -> classDeclaration.accept(this, null))
                .map(it -> (TypeElem)it)
                .forEach(unit::withElement);

        return unit;
    }

    @Override
    public Object visitClassDeclaration(final ClassDeclaration<?> classDeclaration, final Object param) {


        final var typeElement = new TypeElem()
                .withKind(classDeclaration.getKind())
                .withModifiers(classDeclaration.getModifiers())
                .withSimpleName(classDeclaration.getSimpleName().toString());

        classDeclaration.getAnnotations().stream()
                        .map(annotationExpression -> (Annot) annotationExpression.accept(this, param))
                        .forEach(typeElement::withAnnotation);

        classDeclaration.getEnclosed().stream()
                .map(e -> (Elem<?>) e.accept(this, param))
                .forEach(typeElement::withEnclosedElement);
        return typeElement;
    }

    @Override
    public Object visitMethodDeclaration(final MethodDeclaration<?> methodDeclaration, final Object param) {
        final var methodElement = new MethodElem()
                .withKind(methodDeclaration.getKind())
                .withModifiers(methodDeclaration.getModifiers())
                .withSimpleName(methodDeclaration.getSimpleName().toString())
                .withReturnType((TypeExpr) methodDeclaration.getReturnType().accept(this, param));

        methodDeclaration.getParameters().stream()
                .map(methodParam -> (VariableElem) methodParam.accept(this, param))
                .forEach(methodElement::withParameter );

        methodDeclaration.getBody().ifPresent(body -> {
            final var methodBody = (BlockStm) body.accept(this, param);
            methodElement.body(methodBody);
        });

        return methodElement;
    }

    @Override
    public Object visitVariableDeclaration(final VariableDeclaration<?> variableDeclaration, final Object param) {
        final var type = (TypeExpr) variableDeclaration.getVarType().accept(this, param);
        final var initExpr = variableDeclaration.getInitExpression()
                .map(initExpression -> (Expr) initExpression.accept(this, param))
                .orElse(null);

        if (variableDeclaration.getKind() == ElementKind.LOCAL_VARIABLE) {
            return new VariableDeclarationStm()
                    .modifiers(variableDeclaration.getModifiers())
                    .type(type)
                    .identifier(variableDeclaration.getName())
                    .initExpression(initExpr);
        } else {

            return new VariableElem()
                    .withKind(variableDeclaration.getKind())
                    .withType(type)
                    .withSimpleName(variableDeclaration.getName())
                    .withModifiers(variableDeclaration.getModifiers());
        }
    }

    @Override
    public Object visitClassOrInterfaceTypeExpression(final ClassOrInterfaceTypeExpression classOrInterfaceTypeExpression, final Object param) {
        return new ClassOrInterfaceTypeExpr(classOrInterfaceTypeExpression.getName().toString());
    }

    @Override
    public Object visitNoType(final NoTypeExpression noTypeExpression, final Object param) {
        return switch (noTypeExpression.getKind()) {
            case VOID -> new SimpleTypeExpr("void");
            default -> throw new UnsupportedOperationException("Unsupported type: " + noTypeExpression.getKind());
        };
    }

    @Override
    public Object visitAnnotationExpression(final AnnotationExpression annotationExpression, final Object param) {
        final var annot = new Annot(annotationExpression.getAnnotationType().getName().toString());

        annotationExpression.getArguments().forEach((name, value) -> {
            final var annotValue = (Expr) value.accept(this, param);
            annot.withValue(name, annotValue);
        });

        return annot;
    }

    @Override
    public Object visitLiteralExpression(final LiteralExpression literalExpression, final Object param) {
        return switch (literalExpression.getLiteralType()) {
            case STRING -> new StringLiteralExpr(((StringValueLiteralExpression) literalExpression).getValue());
            default -> throw new UnsupportedOperationException("Unsupported literal type: " + literalExpression.getLiteralType());
        };
    }

    @Override
    public Object visitBlockStatement(final BlockStatement blockStatement, final Object param) {
        final var statements = blockStatement.getStatements().stream()
                .map(statement -> (Stm) statement.accept(this, param))
                .toList();
        return new BlockStm(statements);
    }

    @Override
    public Object visitMethodCall(final MethodCallExpression methodCallExpression, final Object param) {
        final var targetExp = methodCallExpression.getTarget()
                .map(target -> (Expr) target.accept(this, param))
                .orElse(null);

        final var arguments = methodCallExpression.getArguments().stream()
                .map(argument -> (Expr) argument.accept(this, param))
                .toList();

        return new MethodInvocationExpr()
                .target(targetExp)
                .name(methodCallExpression.getMethodName().getName())
                .arguments(arguments);
    }

    @Override
    public Object visitIdentifierExpression(final IdentifierExpression identifierExpression, final Object param) {
        return new IdentifierExpr(identifierExpression.getName());
    }

    @Override
    public Object visitIfStatement(final IfStatement ifStatement, final Object param) {
        final var condition = (Expr) ifStatement.getCondition().accept(this, param);
        final var thenStatement = (Stm) ifStatement.getBody().accept(this, param);

        return new IfStm()
                .condition(condition)
                .thenStatement(thenStatement)
                .elseStatement(null);
    }

    @Override
    public Object visitUnaryExpression(final UnaryExpression unaryExpression, final Object param) {
        return new UnaryExpr(
                unaryExpression.getOperator(),
                (Expr) unaryExpression.getExpression().accept(this, param)
        );
    }

    @Override
    public Object visitBinaryExpression(final BinaryExpression binaryExpression, final Object param) {
        return new BinaryExpr()
                .left((Expr) binaryExpression.getLeft().accept(this, param))
                .operator(binaryExpression.getOperator())
                .right((Expr) binaryExpression.getRight().accept(this, param));
    }

    @Override
    public Object visitExpressionStatement(final ExpressionStatement expressionStatement, final Object param) {
        return new ExpressionStm((Expr) expressionStatement.getExpression().accept(this, param));
    }

    @Override
    public Object visitReturnStatement(final ReturnStatement returnStatement, final Object param) {
        return new ReturnStm((Expr) returnStatement.getExpression().accept(this, param));
    }

    @Override
    public Object visitPropertyAccessExpression(final PropertyAccessExpression propertyAccessExpression, final Object param) {
        final var target = (Expr) propertyAccessExpression.getTarget().accept(this, param);
        return new PropertyAccessExpr()
                .withTarget(target)
                .withName(propertyAccessExpression.getName().getName());
    }

    @Override
    public Object visitArrayInitializerExpression(final ArrayInitializerExpression arrayInitializerExpression, final Object param) {
        final var values = arrayInitializerExpression.getValues().stream()
                .map(value -> (Expr) value.accept(this, param))
                .toList();
        return new ArrayExpr()
                .withValues(values);
    }
}
