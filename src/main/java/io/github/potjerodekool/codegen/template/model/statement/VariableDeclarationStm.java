package io.github.potjerodekool.codegen.template.model.statement;

import io.github.potjerodekool.codegen.model.element.Modifier;
import io.github.potjerodekool.codegen.template.model.expression.Expr;
import io.github.potjerodekool.codegen.template.model.type.TypeExpr;

import java.util.*;

public class VariableDeclarationStm implements Stm {

    private final Set<Modifier> modifiers = new LinkedHashSet<>();

    private TypeExpr type;

    private final List<String> identifiers = new ArrayList<>();

    private Expr initExpression;

    public Set<Modifier> getModifiers() {
        return modifiers;
    }


    public VariableDeclarationStm modifier(final Modifier modifier) {
        this.modifiers.add(modifier);
        return this;
    }

    public VariableDeclarationStm modifiers(final Collection<Modifier> modifiers) {
        modifiers.forEach(this::modifier);
        return this;
    }

    public TypeExpr getType() {
        return type;
    }

    public VariableDeclarationStm type(final TypeExpr type) {
        this.type = type;
        return this;
    }

    public List<String> getIdentifiers() {
        return identifiers;
    }

    public VariableDeclarationStm identifier(final String identifier) {
        this.identifiers.add(identifier);
        return this;
    }

    public Expr getInitExpression() {
        return initExpression;
    }

    public VariableDeclarationStm initExpression(final Expr initExpression) {
        this.initExpression = initExpression;
        return this;
    }

    @Override
    public StatementKind getKind() {
        return StatementKind.VARIABLE_DECLARATION;
    }

    @Override
    public <P, R> R accept(final StatementVisitor<P, R> visitor, final P param) {
        return visitor.visitVariableDeclarationStatement(this, param);
    }

    public boolean isFinal() {
        return modifiers.contains(Modifier.FINAL);
    }
}
