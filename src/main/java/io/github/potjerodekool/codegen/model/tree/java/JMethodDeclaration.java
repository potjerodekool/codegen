package io.github.potjerodekool.codegen.model.tree.java;

import io.github.potjerodekool.codegen.model.element.ElementKind;
import io.github.potjerodekool.codegen.model.element.Modifier;
import io.github.potjerodekool.codegen.model.tree.*;
import io.github.potjerodekool.codegen.model.tree.expression.Expression;
import io.github.potjerodekool.codegen.model.tree.statement.BlockStatement;
import io.github.potjerodekool.codegen.model.tree.statement.VariableDeclaration;
import io.github.potjerodekool.codegen.model.type.TypeMirror;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JMethodDeclaration extends MethodDeclaration<JMethodDeclaration> implements JElementTree {

    private final ElementKind kind;
    private final Set<Modifier> modifiers = new HashSet<>();

    public JMethodDeclaration(final CharSequence simpleName,
                              final ElementKind kind,
                              final Expression returnType,
                              final List<TypeParameter> typeParameters,
                              final List<VariableDeclaration<?>> parameters,
                              final @Nullable BlockStatement body) {
        super(simpleName, returnType, typeParameters, parameters, body);
        this.kind = kind;
    }

    public JMethodDeclaration(final ElementKind kind) {
        this.kind = kind;
    }

    public static JMethodDeclaration method() {
        return new JMethodDeclaration(ElementKind.METHOD);
    }

    @Override
    public ElementKind getKind() {
        return kind;
    }

    public JMethodDeclaration modifier(final Modifier modifier) {
        this.modifiers.add(modifier);
        return this;
    }

    public JMethodDeclaration modifiers(final Modifier... modifiers) {
        this.modifiers.addAll(List.of(modifiers));
        return this;
    }

    public JMethodDeclaration modifiers(final List<Modifier> modifiers) {
        this.modifiers.addAll(modifiers);
        return this;
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

    public boolean hasModifier(final Modifier modifier) {
        return modifiers.contains(modifier);
    }

    @Override
    public TypeMirror getType() {
        return null;
    }

    @Override
    public void setType(final TypeMirror type) {

    }

    @Override
    public <R, P> R accept(final TreeVisitor<R, P> visitor, final P param) {
        final var javaTreeVisitor = (JTreeVisitor<R, P>) visitor;
        return javaTreeVisitor.visitMethodDeclaration(this, param);
    }
}
