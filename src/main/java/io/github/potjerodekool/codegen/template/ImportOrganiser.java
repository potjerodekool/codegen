package io.github.potjerodekool.codegen.template;

import io.github.potjerodekool.codegen.template.model.TCompilationUnit;
import io.github.potjerodekool.codegen.template.model.QualifiedImportItem;
import io.github.potjerodekool.codegen.template.model.annotation.Annot;
import io.github.potjerodekool.codegen.template.model.annotation.AnnotationVisitor;
import io.github.potjerodekool.codegen.template.model.element.ElementVisitor;
import io.github.potjerodekool.codegen.template.model.element.MethodElem;
import io.github.potjerodekool.codegen.template.model.element.TypeElem;
import io.github.potjerodekool.codegen.template.model.element.VariableElem;
import io.github.potjerodekool.codegen.template.model.expression.*;
import io.github.potjerodekool.codegen.template.model.statement.*;

public class ImportOrganiser implements ElementVisitor<TCompilationUnit, Void>,
        ExpressionVisitor<TCompilationUnit, Void>,
        StatementVisitor<TCompilationUnit, Void>,
        AnnotationVisitor<TCompilationUnit, Void> {

    public void organiseImports(final TCompilationUnit compilationUnit) {
        compilationUnit.getElements().forEach(element -> element.accept(this, compilationUnit));
    }

    //Elements
    @Override
    public Void visitExecutableElement(final MethodElem methodElement,
                                       final TCompilationUnit compilationUnit) {
        methodElement.getAnnotations().forEach(annotation -> annotation.accept((AnnotationVisitor) this, compilationUnit));

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
                                 final TCompilationUnit compilationUnit) {
        typeElement.getAnnotations().forEach(annotation -> annotation.accept((AnnotationVisitor)this, compilationUnit));
        typeElement.getEnclosedElements().forEach(element -> element.accept(this, compilationUnit));
        return null;
    }

    @Override
    public Void visitVariableElement(final VariableElem variableElement, final TCompilationUnit compilationUnit) {
        variableElement.getAnnotations().forEach(annotation -> annotation.accept((AnnotationVisitor) this, compilationUnit));
        variableElement.getType().accept(this, compilationUnit);
        return null;
    }

    //Expressions
    @Override
    public Void visitBinaryExpression(final BinaryExpr binaryExpression, final TCompilationUnit compilationUnit) {
        binaryExpression.getLeft().accept(this, compilationUnit);
        binaryExpression.getRight().accept(this, compilationUnit);
        return null;
    }

    @Override
    public Void visitClassOrInterfaceTypeExpression(final ClassOrInterfaceTypeExpr classOrInterfaceTypeExpression,
                                                    final TCompilationUnit compilationUnit) {
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

    private boolean addImport(final TCompilationUnit compilationUnit, final String importName) {
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
            compilationUnit.importItem(importName);
            return true;
        } else {
            final var qualifiedCount = importItems.stream()
                    .filter(importItem -> importItem instanceof QualifiedImportItem)
                    .count();
            return qualifiedCount == 1;
        }
    }

    @Override
    public Void visitIdentifierExpression(final IdentifierExpr identifierExpression, final TCompilationUnit compilationUnit) {
        return null;
    }

    @Override
    public Void visitMethodInvocationExpression(final MethodInvocationExpr methodInvocationExpression, final TCompilationUnit compilationUnit) {
        if (methodInvocationExpression.getTarget() != null) {
            methodInvocationExpression.getTarget().accept(this, compilationUnit);
        }

        methodInvocationExpression.getArguments().forEach(argument -> argument.accept(this, compilationUnit));
        return null;
    }

    @Override
    public Void visitUnaryExpression(final UnaryExpr unaryExpression, final TCompilationUnit compilationUnit) {
        unaryExpression.getExpression().accept(this, compilationUnit);
        return null;
    }

    @Override
    public Void visitLiteralExpression(final LiteralExpr literalExpression, final TCompilationUnit compilationUnit) {
        return null;
    }

    @Override
    public Void visitArrayExpression(final ArrayExpr arrayExpression, final TCompilationUnit compilationUnit) {
        if (arrayExpression.getComponentType() != null) {
            arrayExpression.getComponentType().accept(this, compilationUnit);
        }
        arrayExpression.getValues().forEach(expression -> expression.accept(this, compilationUnit));
        return null;
    }

    @Override
    public Void visitPropertyAccessExpression(final PropertyAccessExpr propertyAccessExpression, final TCompilationUnit compilationUnit) {
        if (propertyAccessExpression.getTarget() != null) {
            propertyAccessExpression.getTarget().accept(this, compilationUnit);
        }
        return null;
    }

    @Override
    public Void visitTypeExpression(final SimpleTypeExpr simpleTypeExpr, final TCompilationUnit compilationUnit) {
        return null;
    }

    //Statements
    @Override
    public Void visitBlockStatement(final BlockStm blockStatement, final TCompilationUnit compilationUnit) {
        blockStatement.getStatements().forEach(statement -> statement.accept(this, compilationUnit));
        return null;
    }

    @Override
    public Void visitIfStatement(final IfStm ifStatement, final TCompilationUnit compilationUnit) {
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
    public Void visitReturnStatement(final ReturnStm returnStatement, final TCompilationUnit compilationUnit) {
        returnStatement.getExpression().accept(this, compilationUnit);
        return null;
    }

    @Override
    public Void visitStatementExpression(final ExpressionStm statementExpression, final TCompilationUnit compilationUnit) {
        statementExpression.getExpression().accept(this, compilationUnit);
        return null;
    }

    @Override
    public Void visitVariableDeclarationStatement(final VariableDeclarationStm variableDeclarationStatement, final TCompilationUnit compilationUnit) {
        variableDeclarationStatement.getType().accept(this, compilationUnit);
        if (variableDeclarationStatement.getInitExpression() != null) {
            variableDeclarationStatement.getInitExpression().accept(this, compilationUnit);
        }
        return null;
    }

    //Annotations

    @Override
    public Void visitAnnotation(final Annot annotation, final TCompilationUnit compilationUnit) {
        if (addImport(compilationUnit, annotation.getName())) {
            annotation.name(getSimpleName(annotation.getName()));
        }
        return null;
    }

    @Override
    public Void visitFieldAccessExpression(final FieldAccessExpr fieldAccessExpr, final TCompilationUnit compilationUnit) {
        return null;
    }

    @Override
    public Void visitNewClassExpression(final NewClassExpr newClassExpr, final TCompilationUnit compilationUnit) {
        return null;
    }
}
