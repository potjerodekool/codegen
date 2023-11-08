package io.github.potjerodekool.codegen.model.tree.statement.java;

import io.github.potjerodekool.codegen.model.element.ElementKind;
import io.github.potjerodekool.codegen.model.element.Modifier;
import io.github.potjerodekool.codegen.model.symbol.AbstractSymbol;
import io.github.potjerodekool.codegen.model.tree.JTreeVisitor;
import io.github.potjerodekool.codegen.model.tree.TreeVisitor;
import io.github.potjerodekool.codegen.model.tree.expression.Expression;
import io.github.potjerodekool.codegen.model.tree.statement.VariableDeclaration;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JVariableDeclaration extends VariableDeclaration<JVariableDeclaration> {

    private final ElementKind kind;

    private final Set<Modifier> modifiers = new HashSet<>();

    public JVariableDeclaration(final ElementKind kind,
                                final Set<Modifier> modifiers,
                                final Expression varType,
                                final String name,
                                final @Nullable Expression initExpression,
                                final AbstractSymbol symbol) {
        super(varType, name, initExpression, symbol);
        this.kind = kind;
        this.modifiers.addAll(modifiers);
    }

    public JVariableDeclaration(final ElementKind kind) {
        this.kind = kind;
    }

    public static JVariableDeclaration parameter() {
        return new JVariableDeclaration(ElementKind.PARAMETER);
    }

    @Override
    public ElementKind getKind() {
        return kind;
    }

    @Override
    public Set<Modifier> getModifiers() {
        return modifiers;
    }

    public JVariableDeclaration modifier(final Modifier modifier) {
        this.modifiers.add(modifier);
        return this;
    }

    public JVariableDeclaration modifiers(final Modifier... modifiers) {
        this.modifiers.addAll(List.of(modifiers));
        return this;
    }

    @Override
    public <R, P> R accept(final TreeVisitor<R, P> visitor, final P param) {
        final var javaTreeVisitor = (JTreeVisitor<R,P>) visitor;
        return javaTreeVisitor.visitVariableDeclaration(this, param);
    }
}
