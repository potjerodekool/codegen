package io.github.potjerodekool.codegen.model.tree.statement.kotlin;

import io.github.potjerodekool.codegen.model.element.ElementKind;
import io.github.potjerodekool.codegen.model.element.Modifier;
import io.github.potjerodekool.codegen.model.symbol.AbstractSymbol;
import io.github.potjerodekool.codegen.model.tree.expression.Expression;
import io.github.potjerodekool.codegen.model.tree.statement.VariableDeclaration;

import java.util.HashSet;
import java.util.Set;

public class KVariableDeclaration extends VariableDeclaration<KVariableDeclaration> {

    private final ElementKind kind;

    private final Set<Modifier> modifiers = new HashSet<>();

    public KVariableDeclaration(final ElementKind kind,
                                final Set<Modifier> modifiers,
                                final Expression varType,
                                final String name,
                                final Expression initExpression,
                                final AbstractSymbol symbol) {
        super(varType, name, initExpression, symbol);
        this.kind = kind;
        this.modifiers.addAll(modifiers);
    }

    @Override
    public ElementKind getKind() {
        return kind;
    }

    @Override
    public Set<Modifier> getModifiers() {
        return modifiers;
    }

    public void addModifier(final Modifier modifier) {
        this.modifiers.add(modifier);
    }

    public void removeModifier(final Modifier modifier) {
        this.modifiers.remove(modifier);
    }

}
