package io.github.potjerodekool.codegen.model.tree.statement.kotlin;

import io.github.potjerodekool.codegen.model.element.ElementKind;
import io.github.potjerodekool.codegen.model.element.Modifier;
import io.github.potjerodekool.codegen.model.element.Name;
import io.github.potjerodekool.codegen.model.symbol.ClassSymbol;
import io.github.potjerodekool.codegen.model.tree.KTreeVisitor;
import io.github.potjerodekool.codegen.model.tree.TreeVisitor;
import io.github.potjerodekool.codegen.model.tree.expression.Expression;
import io.github.potjerodekool.codegen.model.tree.kotlin.KMethodDeclaration;
import io.github.potjerodekool.codegen.model.tree.statement.BlockStatement;
import io.github.potjerodekool.codegen.model.tree.statement.ClassDeclaration;

import java.util.*;

public class KClassDeclaration extends ClassDeclaration<KClassDeclaration> {

    private final ElementKind kind;

    private final Set<Modifier> modifiers = new HashSet<>();

    private ClassSymbol classSymbol;

    public KClassDeclaration(final Name simpleName,
                             final ElementKind kind,
                             final Set<Modifier> modifiers) {
        super(simpleName);
        this.kind = kind;
        addModifiers(modifiers);
    }

    @Override
    public ElementKind getKind() {
        return kind;
    }

    public void addModifier(final Modifier modifier) {
        this.modifiers.add(modifier);
    }

    public void addModifiers(final Modifier... modifiers) {
        addModifiers(List.of(modifiers));
    }

    public void addModifiers(final Collection<Modifier> modifiers) {
        this.modifiers.addAll(modifiers);
    }

    public void removeModifier(final Modifier modifier) {
        this.modifiers.remove(modifier);
    }

    @Override
    public Set<Modifier> getModifiers() {
        return modifiers;
    }

    @Override
    public ClassSymbol getClassSymbol() {
        return classSymbol;
    }

    public void setClassSymbol(final ClassSymbol classSymbol) {
        this.classSymbol = classSymbol;
    }

    public KMethodDeclaration addPrimaryConstructor() {
        final var primaryConstructor = new KMethodDeclaration(
                getSimpleName(),
                ElementKind.CONSTRUCTOR,
                null,
                new ArrayList<>(),
                new ArrayList<>(),
                new BlockStatement()
        );
        setPrimaryConstructor(primaryConstructor);
        return primaryConstructor;
    }

    public KMethodDeclaration addConstructor() {
        final var constructor = new KMethodDeclaration(
                getSimpleName(),
                ElementKind.CONSTRUCTOR,
                null,
                new ArrayList<>(),
                new ArrayList<>(),
                new BlockStatement()
        );
        addEnclosed(constructor);
        return constructor;
    }

    public KMethodDeclaration addMethod(final Expression returnType,
                                        final CharSequence name,
                                        final Set<Modifier> modifiers) {
        final var method = new KMethodDeclaration(
                name,
                ElementKind.METHOD,
                returnType,
                new ArrayList<>(),
                new ArrayList<>(),
                null
        );
        method.addModifiers(modifiers);
        addEnclosed(method);
        return method;
    }

    @Override
    public List<KMethodDeclaration> constructors() {
        return getEnclosed().stream()
                .filter(enclosed -> enclosed instanceof KMethodDeclaration method
                        && method.getKind() == ElementKind.CONSTRUCTOR
                )
                .map(enclosed -> (KMethodDeclaration) enclosed)
                .toList();
    }

    @Override
    public List<KMethodDeclaration> methods() {
        return getEnclosed().stream()
                .filter(enclosed -> enclosed instanceof KMethodDeclaration method
                    && method.getKind() == ElementKind.METHOD
                )
                .map(enclosed -> (KMethodDeclaration) enclosed)
                .toList();
    }

    @Override
    public List<KVariableDeclaration> fields() {
        return getEnclosed().stream()
                .filter(enclosed -> enclosed instanceof KVariableDeclaration variableDeclaration
                        && variableDeclaration.getKind() == ElementKind.FIELD
                )
                .map(enclosed -> (KVariableDeclaration) enclosed)
                .toList();
    }

    @Override
    public <R, P> R accept(final TreeVisitor<R, P> visitor, final P param) {
        final var kotlinTreeVisitor = (KTreeVisitor<R, P>) visitor;
        return kotlinTreeVisitor.visitClassDeclaration(this, param);
    }

}
