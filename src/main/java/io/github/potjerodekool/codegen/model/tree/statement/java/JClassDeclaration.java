package io.github.potjerodekool.codegen.model.tree.statement.java;

import io.github.potjerodekool.codegen.model.element.*;
import io.github.potjerodekool.codegen.model.symbol.ClassSymbol;
import io.github.potjerodekool.codegen.model.tree.expression.Expression;
import io.github.potjerodekool.codegen.model.tree.java.JMethodDeclaration;
import io.github.potjerodekool.codegen.model.tree.statement.BlockStatement;
import io.github.potjerodekool.codegen.model.tree.statement.ClassDeclaration;
import io.github.potjerodekool.codegen.model.tree.type.NoTypeExpression;
import io.github.potjerodekool.codegen.model.type.TypeKind;

import java.util.*;

public class JClassDeclaration extends ClassDeclaration<JClassDeclaration> {

    private final Set<Modifier> modifiers = new LinkedHashSet<>();

    private final ElementKind kind;

    private ClassSymbol classSymbol;

    public JClassDeclaration(final CharSequence simpleName,
                             final ElementKind kind) {
        super(simpleName);
        this.kind = kind;
    }

    @Override
    public ElementKind getKind() {
        return kind;
    }

    public JClassDeclaration modifier(final Modifier modifier) {
        this.modifiers.add(modifier);
        return this;
    }

    public JClassDeclaration modifiers(final Modifier... modifiers) {
        for (final Modifier modifier : modifiers) {
            modifier(modifier);
        }
        return this;
    }

    public JClassDeclaration modifiers(final Set<Modifier> modifiers) {
        for (final Modifier modifier : modifiers) {
            modifier(modifier);
        }
        return this;
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

    @Override
    public void setClassSymbol(final ClassSymbol classSymbol) {
        this.classSymbol = classSymbol;
    }

    public JMethodDeclaration addPrimaryConstructor() {
        final var primaryConstructor = new JMethodDeclaration(
                getSimpleName(),
                ElementKind.CONSTRUCTOR,
                new NoTypeExpression(TypeKind.VOID),
                new ArrayList<>(),
                new ArrayList<>(),
                new BlockStatement()
        );
        setPrimaryConstructor(primaryConstructor);
        primaryConstructor.setEnclosing(this);
        return primaryConstructor;
    }

    public JMethodDeclaration addConstructor() {
        final var constructor = new JMethodDeclaration(
                getSimpleName(),
                ElementKind.CONSTRUCTOR,
                new NoTypeExpression(TypeKind.VOID),
                new ArrayList<>(),
                new ArrayList<>(),
                new BlockStatement()
        );
        addEnclosed(constructor);
        constructor.setEnclosing(this);
        return constructor;
    }

    public JMethodDeclaration addMethod() {
        final var method = new JMethodDeclaration(ElementKind.METHOD);
        addEnclosed(method);
        method.setEnclosing(this);
        return method;
    }

    public JMethodDeclaration addMethod(final Expression returnType,
                                        final CharSequence name,
                                        final Set<Modifier> modifiers) {
        final var method = new JMethodDeclaration(
                name,
                ElementKind.METHOD,
                returnType,
                new ArrayList<>(),
                new ArrayList<>(),
                null
        );
        method.addModifiers(modifiers);
        addEnclosed(method);
        method.setEnclosing(this);
        return method;
    }

    @Override
    public List<JMethodDeclaration> constructors() {
        return getEnclosed().stream()
                .filter(enclosed -> enclosed instanceof JMethodDeclaration method
                        && method.getKind() == ElementKind.CONSTRUCTOR
                )
                .map(enclosed -> (JMethodDeclaration) enclosed)
                .toList();
    }

    @Override
    public List<JMethodDeclaration> methods() {
        return getEnclosed().stream()
                .filter(enclosed -> enclosed instanceof JMethodDeclaration method
                        && method.getKind() == ElementKind.METHOD
                )
                .map(enclosed -> (JMethodDeclaration) enclosed)
                .toList();
    }

    @Override
    public List<JVariableDeclaration> fields() {
        return getEnclosed().stream()
                .filter(enclosed -> enclosed instanceof JVariableDeclaration variableDeclaration
                        && variableDeclaration.getKind() == ElementKind.FIELD
                )
                .map(enclosed -> (JVariableDeclaration) enclosed)
                .toList();
    }
}
