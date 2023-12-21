package io.github.potjerodekool.codegen.template;

import io.github.potjerodekool.codegen.template.model.CompilationUnit;
import io.github.potjerodekool.codegen.template.model.QualifiedImportItem;
import io.github.potjerodekool.codegen.template.model.annotation.Annot;
import io.github.potjerodekool.codegen.template.model.annotation.AnnotationVisitor;
import io.github.potjerodekool.codegen.template.model.element.ElementVisitor;
import io.github.potjerodekool.codegen.template.model.element.MethodElem;
import io.github.potjerodekool.codegen.template.model.element.TypeElem;
import io.github.potjerodekool.codegen.template.model.element.VariableElem;
import io.github.potjerodekool.codegen.template.model.expression.*;
import io.github.potjerodekool.codegen.template.model.statement.*;

public class ImportOrganiser implements ElementVisitor<CompilationUnit, Void>,
        ExpressionVisitor<CompilationUnit, Void>,
        StatementVisitor<CompilationUnit, Void>,
        AnnotationVisitor<CompilationUnit, Void> {

    public void organiseImports(final CompilationUnit compilationUnit) {
        compilationUnit.getElements().forEach(element -> {
            element.accept(this, compilationUnit);
        });
    }

    //Elements
    @Override
    public Void visitExecutableElement(final MethodElem methodElement,
                                       final CompilationUnit compilationUnit) {
        methodElement.getAnnotations().forEach(annotation -> annotation.accept(this, compilationUnit));

        if (methodElement.getReturnType() != null) {
            methodElement.getReturnType().accept(this, compilationUnit);
        }

        methodElement.getParameters().forEach(parameter -> parameter.accept(this, compilationUnit));

        if (methodElement.getBody() != null) {
            methodElement.getBody().accept(this, compilationUnit);
        }

        return null;
    }

    @Override
    public Void visitTypeElement(final TypeElem typeElement,
                                 final CompilationUnit compilationUnit) {
        typeElement.getAnnotations().forEach(annotation -> annotation.accept(this, compilationUnit));
        typeElement.getEnclosedElements().forEach(element -> element.accept(this, compilationUnit));
        return null;
    }

    @Override
    public Void visitVariableElement(final VariableElem variableElement, final CompilationUnit compilationUnit) {
        variableElement.getAnnotations().forEach(annotation -> annotation.accept(this, compilationUnit));
        variableElement.getType().accept(this, compilationUnit);
        return null;
    }

    //Expressions
    @Override
    public Void visitBinaryExpression(final BinaryExpr binaryExpression, final CompilationUnit compilationUnit) {
        binaryExpression.getLeft().accept(this, compilationUnit);
        binaryExpression.getRight().accept(this, compilationUnit);
        return null;
    }

    @Override
    public Void visitClassOrInterfaceTypeExpression(final ClassOrInterfaceTypeExpr classOrInterfaceTypeExpression,
                                                    final CompilationUnit compilationUnit) {
        if (addImport(compilationUnit, classOrInterfaceTypeExpression.getName())) {
            final var simpleName = getSimpleName(classOrInterfaceTypeExpression.getName());
            classOrInterfaceTypeExpression.name(simpleName);
        }

        return null;
    }

    private String getSimpleName(final String className) {
        final var sepIndex = className.lastIndexOf('.');
        return sepIndex < 0 ? className : className.substring(sepIndex + 1);
    }

    private boolean addImport(final CompilationUnit compilationUnit, final String importName) {
        final var packageNameEnd = importName.lastIndexOf(".");
        if (packageNameEnd > 0) {
            final var packageName = importName.substring(0, packageNameEnd);

            if ("kotlin".equals(packageName) || "java.lang".equals(packageName)) {
                return true;
            }
        }

        final var importItems =  compilationUnit.getImports().stream()
                .filter(importItem -> importItem.isImportFor(importName))
                .toList();

        if (importItems.isEmpty()) {
            compilationUnit.withImport(importName);
            return true;
        } else {
            final var qualifiedCount = importItems.stream()
                    .filter(importItem -> importItem instanceof QualifiedImportItem)
                    .count();
            return qualifiedCount == 1;
        }
    }

    @Override
    public Void visitIdentifierExpression(final IdentifierExpr identifierExpression, final CompilationUnit compilationUnit) {
        return null;
    }

    @Override
    public Void visitMethodInvocationExpression(final MethodInvocationExpr methodInvocationExpression, final CompilationUnit compilationUnit) {
        if (methodInvocationExpression.getTarget() != null) {
            methodInvocationExpression.getTarget().accept(this, compilationUnit);
        }

        methodInvocationExpression.getArguments().forEach(argument -> argument.accept(this, compilationUnit));
        return null;
    }

    @Override
    public Void visitUnaryExpression(final UnaryExpr unaryExpression, final CompilationUnit compilationUnit) {
        unaryExpression.getExpression().accept(this, compilationUnit);
        return null;
    }

    @Override
    public <T> Void visitLiteralExpression(final LiteralExpr<?> tLiteralExpression, final CompilationUnit compilationUnit) {
        return null;
    }

    @Override
    public Void visitArrayExpression(final ArrayExpr arrayExpression, final CompilationUnit compilationUnit) {
        if (arrayExpression.getComponentType() != null) {
            arrayExpression.getComponentType().accept(this, compilationUnit);
        }
        arrayExpression.getValues().forEach(expression -> expression.accept(this, compilationUnit));
        return null;
    }

    @Override
    public Void visitPropertyAccessExpression(final PropertyAccessExpr propertyAccessExpression, final CompilationUnit compilationUnit) {
        if (propertyAccessExpression.getTarget() != null) {
            propertyAccessExpression.getTarget().accept(this, compilationUnit);
        }
        return null;
    }

    @Override
    public Void visitTypeExpression(final SimpleTypeExpr simpleTypeExpr, final CompilationUnit compilationUnit) {
        return null;
    }

    //Statements
    @Override
    public Void visitBlockStatement(final BlockStm blockStatement, final CompilationUnit compilationUnit) {
        blockStatement.getStatements().forEach(statement -> statement.accept(this, compilationUnit));
        return null;
    }

    @Override
    public Void visitIfStatement(final IfStm ifStatement, final CompilationUnit compilationUnit) {
        ifStatement.getCondition().accept(this, compilationUnit);
        if (ifStatement.getThenStatement() != null) {
            ifStatement.getThenStatement().accept(this, compilationUnit);
        }

        if (ifStatement.getElseStatement() != null) {
            ifStatement.getElseStatement().accept(this, compilationUnit);
        }
        return null;
    }

    @Override
    public Void visitReturnStatement(final ReturnStm returnStatement, final CompilationUnit compilationUnit) {
        returnStatement.getExpression().accept(this, compilationUnit);
        return null;
    }

    @Override
    public Void visitStatementExpression(final ExpressionStm statementExpression, final CompilationUnit compilationUnit) {
        statementExpression.getExpression().accept(this, compilationUnit);
        return null;
    }

    @Override
    public Void visitVariableDeclarationStatement(final VariableDeclarationStm variableDeclarationStatement, final CompilationUnit compilationUnit) {
        variableDeclarationStatement.getType().accept(this, compilationUnit);
        if (variableDeclarationStatement.getInitExpression() != null) {
            variableDeclarationStatement.getInitExpression().accept(this, compilationUnit);
        }
        return null;
    }

    //Annotations

    @Override
    public Void visitAnnotation(final Annot annotation, final CompilationUnit compilationUnit) {
        if (addImport(compilationUnit, annotation.getName())) {
            annotation.name(getSimpleName(annotation.getName()));
        }
        return null;
    }

    @Override
    public Void visitFieldAccessExpression(final FieldAccessExpr fieldAccessExpr, final CompilationUnit compilationUnit) {
        return null;
    }
}
