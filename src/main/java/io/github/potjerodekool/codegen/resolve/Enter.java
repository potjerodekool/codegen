package io.github.potjerodekool.codegen.resolve;

import io.github.potjerodekool.codegen.model.CompilationUnit;
import io.github.potjerodekool.codegen.model.CompilationUnitVisitor;
import io.github.potjerodekool.codegen.model.CompositeScope;
import io.github.potjerodekool.codegen.model.element.Name;
import io.github.potjerodekool.codegen.model.element.NestingKind;
import io.github.potjerodekool.codegen.model.symbol.MethodSymbol;
import io.github.potjerodekool.codegen.model.symbol.VariableSymbol;
import io.github.potjerodekool.codegen.model.tree.MethodDeclaration;
import io.github.potjerodekool.codegen.model.tree.PackageDeclaration;
import io.github.potjerodekool.codegen.model.tree.TreeVisitor;
import io.github.potjerodekool.codegen.model.tree.statement.ClassDeclaration;
import io.github.potjerodekool.codegen.model.tree.statement.VariableDeclaration;
import io.github.potjerodekool.codegen.model.util.SymbolTable;

public class Enter implements
        CompilationUnitVisitor<Object, Scope>,
        TreeVisitor<Object, Scope> {

    private final SymbolTable symbolTable;

    public Enter(final SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    @Override
    public Object visitCompilationUnit(final CompilationUnit compilationUnit,
                                       final Scope scope) {
        final var compositeScope = new CompositeScope();
        compilationUnit.getDefinitions().forEach(definition -> definition.accept(this, compositeScope));
        return null;
    }

    @Override
    public Object visitPackageDeclaration(final PackageDeclaration packageDeclaration, final Scope scope) {
        final var packageSymbol = symbolTable.enterPackage(null, Name.of(packageDeclaration.getName().getName()));
        packageDeclaration.setPackageSymbol(packageSymbol);
        packageSymbol.scope = new WritableScope(packageSymbol, scope);
        return null;
    }

    @Override
    public Object visitClassDeclaration(final ClassDeclaration classDeclaration,
                                        final Scope scope) {
        final var enclosing = classDeclaration.getEnclosing();
        final var nestingKind = enclosing instanceof PackageDeclaration
                ? NestingKind.TOP_LEVEL
                : NestingKind.MEMBER;

        final var classSymbol = symbolTable.enterClass(null, classDeclaration.getQualifiedName());
        classSymbol.setKind(classDeclaration.getKind());
        classSymbol.setNestingKind(nestingKind);
        classDeclaration.classSymbol(classSymbol);

        final var classScope = new WritableScope(classSymbol, scope);
        classSymbol.scope = classScope;

        classDeclaration.getEnclosed().forEach(enclosed -> enclosed.accept(this, classScope));
        return null;
    }

    @Override
    public Object visitMethodDeclaration(final MethodDeclaration methodDeclaration, final Scope scope) {
        final var methodSymbol = new MethodSymbol(
                methodDeclaration.getKind(),
                null,
                methodDeclaration.getSimpleName()
        );

        methodSymbol.addModifiers(methodDeclaration.getModifiers());

        methodDeclaration.methodSymbol(methodSymbol);

        final var methodScope = new WritableScope(methodSymbol, scope);
        methodSymbol.scope = methodScope;

        methodDeclaration.getTypeParameters().forEach(typeParam -> typeParam.accept(this, methodScope));
        methodDeclaration.getParameters().forEach(parameter -> parameter.accept(this, methodScope));

        final var parameters = methodDeclaration.getParameters().stream()
                .map(parameter ->  (VariableSymbol) parameter.getSymbol())
                .toList();

        methodSymbol.addParameters(parameters);

        return null;
    }

    @Override
    public Object visitVariableDeclaration(final VariableDeclaration variableDeclaration,
                                           final Scope scope) {
        final var variableSymbol = new VariableSymbol(variableDeclaration.getKind(), variableDeclaration.getName());
        variableSymbol.addModifiers(variableDeclaration.getModifiers());
        variableDeclaration.symbol(variableSymbol);

        scope.define(variableSymbol);

        return null;
    }
}
