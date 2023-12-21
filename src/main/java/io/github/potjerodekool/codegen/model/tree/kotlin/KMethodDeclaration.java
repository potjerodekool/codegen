package io.github.potjerodekool.codegen.model.tree.kotlin;

import io.github.potjerodekool.codegen.model.element.ElementKind;
import io.github.potjerodekool.codegen.model.element.Modifier;
import io.github.potjerodekool.codegen.model.tree.*;
import io.github.potjerodekool.codegen.model.tree.expression.Expression;
import io.github.potjerodekool.codegen.model.tree.statement.BlockStatement;
import io.github.potjerodekool.codegen.model.tree.statement.kotlin.KVariableDeclaration;
import io.github.potjerodekool.codegen.model.type.TypeMirror;

import java.util.*;

public class KMethodDeclaration extends MethodDeclaration<KMethodDeclaration> {

    private final ElementKind kind;
    private final Set<Modifier> modifiers = new LinkedHashSet<>();

    public KMethodDeclaration(final CharSequence simpleName,
                              final ElementKind kind,
                              final Expression returnType,
                              final List<TypeParameter> typeParameters,
                              final List<KVariableDeclaration> parameters,
                              final BlockStatement body) {
        super(simpleName, returnType, typeParameters, parameters, body);
        this.kind = kind;
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
        return visitor.visitMethodDeclaration(this, param);
    }
}
