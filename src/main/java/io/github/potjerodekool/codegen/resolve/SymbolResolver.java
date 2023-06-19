package io.github.potjerodekool.codegen.resolve;

import io.github.potjerodekool.codegen.model.AbstractAstVisitor;
import io.github.potjerodekool.codegen.model.CompilationUnit;
import io.github.potjerodekool.codegen.DefaultDiagnostic;
import io.github.potjerodekool.codegen.Diagnostic;
import io.github.potjerodekool.codegen.model.element.*;
import io.github.potjerodekool.codegen.model.tree.MethodDeclaration;
import io.github.potjerodekool.codegen.model.tree.type.AnnotatedTypeExpression;
import io.github.potjerodekool.codegen.model.tree.expression.NameExpression;
import io.github.potjerodekool.codegen.model.tree.type.PrimitiveTypeExpression;
import io.github.potjerodekool.codegen.model.tree.statement.ClassDeclaration;
import io.github.potjerodekool.codegen.model.tree.statement.VariableDeclaration;
import io.github.potjerodekool.codegen.model.tree.statement.BlockStatement;
import io.github.potjerodekool.codegen.model.tree.statement.IfStatement;
import io.github.potjerodekool.codegen.model.symbol.AbstractSymbol;
import io.github.potjerodekool.codegen.model.type.TypeMirror;
import io.github.potjerodekool.codegen.model.util.Elements;
import io.github.potjerodekool.codegen.model.util.type.Types;

import java.util.Optional;

public class SymbolResolver extends AbstractAstVisitor<Void, Scope> {

    private final Elements elements;

    private final Types types;

    public SymbolResolver(final Elements elements, final Types types) {
        this.elements = elements;
        this.types = types;
    }

    public void resolveSymbols(final CompilationUnit compilationUnit) {
        final var scope = new GlobalScope(this.elements, compilationUnit);
        resolveImports(compilationUnit, scope);
        compilationUnit.getElements().forEach(it -> it.accept(this, scope));
        compilationUnit.getDefinitions().forEach(it -> it.accept(this, scope));
    }

    private void resolveImports(final CompilationUnit compilationUnit,
                                final Scope scope) {
        compilationUnit.getImports().forEach(importStm -> {
            final var typeElement = elements.getTypeElement(importStm);

            if (typeElement != null) {
                scope.define(importStm, (AbstractSymbol<?>) typeElement);
            }
        });
    }

    @Override
    public Void visitType(final TypeElement e,
                          final Scope scope) {
        final Scope currentScope;

        if (e.getNestingKind() == NestingKind.TOP_LEVEL) {
            currentScope = scope;
        } else {
            currentScope = scope.child();
        }

        currentScope.define("this", (AbstractSymbol<?>) e);
        return super.visitType(e, currentScope);
    }

    @Override
    public Void visitVariable(final VariableElement e,
                              final Scope scope) {
        scope.define((AbstractSymbol<?>) e);
        return super.visitVariable(e, scope);
    }

    @Override
    public Void visitVariableDeclaration(final VariableDeclaration variableDeclaration,
                                         final Scope scope) {
        variableDeclaration.getVarType().accept(this, scope);

        if (variableDeclaration.getSymbol() != null) {
            scope.define(variableDeclaration.getSymbol());
        }
        return super.visitVariableDeclaration(variableDeclaration, scope);
    }

    @Override
    public Void visitClassDeclaration(final ClassDeclaration classDeclaration, final Scope scope) {
        classDeclaration.getEnclosed().forEach(it -> it.accept(this, scope));
        return null;
    }

    @Override
    public Void visitMethodDeclaration(final MethodDeclaration methodDeclaration, final Scope scope) {
        methodDeclaration.getAnnotations().forEach(it -> it.accept(this, scope));
        methodDeclaration.getTypeParameters().forEach(it -> it.accept(this, scope));
        methodDeclaration.getParameters().forEach(it -> it.accept(this, scope));
        methodDeclaration.getBody().ifPresent(it -> it.accept(this, scope));
        methodDeclaration.getReturnType().accept(this, scope);
        return null;
    }

    @Override
    public Void visitAnnotatedType(final AnnotatedTypeExpression annotatedTypeExpression, final Scope scope) {
        /*
        annotatedTypeExpression.getTypeArguments().forEach(it -> {
            final var typeArgName = (NameExpression) it;
            resolveType(typeArgName, scope);
        });
        annotatedTypeExpression.getAnnotations().forEach(it -> it.accept(this, scope));

        resolveType((NameExpression) annotatedTypeExpression.getIdentifier(), scope);

        if (!annotatedTypeExpression.getTypeArguments().isEmpty()) {
            final var identType = annotatedTypeExpression.getIdentifier().getType();

            if (identType != null) {
                final var element = (TypeElement) types.asElement(identType);
                final var typeArgs = annotatedTypeExpression.getTypeArguments().stream()
                        .map(Tree::getType)
                        .toArray(TypeMirror[]::new);
                annotatedTypeExpression.setType(types.getDeclaredType(element, typeArgs));
            }
        }
         */
        return null;
    }

    private void resolveType(final NameExpression nameExpression,
                             final Scope scope) {
        final var identSymbolOptional = scope.resolveSymbol(nameExpression.getName());
        final TypeMirror type;

        if (identSymbolOptional.isPresent()) {
            final var symbol = identSymbolOptional.get();
            nameExpression.setType(symbol.asType());
        }
    }

    @Override
    public Void visitExecutable(final ExecutableElement e,
                                final Scope scope) {
        final var childScope = scope.child();
        return super.visitExecutable(e, childScope);
    }

    @Override
    public Void visitNameExpression(final NameExpression nameExpression,
                                    final Scope scope) {
        resolveNameExpressionSymbol(nameExpression.getName(), scope).ifPresentOrElse(
                nameExpression::setSymbol,
                () -> {
                    final var globalScope = scope.findGlobalScope();
                    final var fileObject = globalScope.getCompilationUnit().getFileObject();
                    new DefaultDiagnostic<>(
                            Diagnostic.Kind.ERROR,
                            String.format("Failed to resolve %s", nameExpression.getName()),
                            fileObject
                    );
                });
        return null;
    }

    private Optional<AbstractSymbol<?>> resolveNameExpressionSymbol(final String name,
                                                                    final Scope scope) {
        final var symbolOptional = scope.resolveSymbol(name);

        if (symbolOptional.isPresent()) {
            return symbolOptional;
        } else {
            final var sepIndex = name.lastIndexOf('.');

            if (sepIndex == -1) {
                return Optional.empty();
            } else {
                final var enclosingName = name.substring(0, sepIndex);
                final var enclosedName = name.substring(sepIndex + 1);
                return resolveNameExpressionSymbol(enclosingName + "$" + enclosedName, scope);
            }
        }
    }


    @Override
    public Void visitIfStatement(final IfStatement ifStatement, final Scope scope) {
        ifStatement.getCondition().accept(this, scope);

        final var childScope = scope.child();
        ifStatement.getBody().accept(this, childScope);
        return null;
    }

    @Override
    public Void visitBlockStatement(final BlockStatement blockStatement,
                                    final Scope scope) {
        final var childScope = scope.child();
        blockStatement.getStatements().forEach(it -> it.accept(this, childScope));
        return null;
    }

    @Override
    public Void visitPrimitiveTypeExpression(final PrimitiveTypeExpression primitiveTypeExpression, final Scope param) {
        throw new UnsupportedOperationException();
    }
}

