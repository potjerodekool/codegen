package io.github.potjerodekool.codegen.model.tree.statement;

import io.github.potjerodekool.codegen.model.element.ElementKind;
import io.github.potjerodekool.codegen.model.element.Modifier;
import io.github.potjerodekool.codegen.model.symbol.AbstractSymbol;
import io.github.potjerodekool.codegen.model.tree.AnnotationExpression;
import io.github.potjerodekool.codegen.model.tree.TreeVisitor;
import io.github.potjerodekool.codegen.model.tree.WithMetaData;
import io.github.potjerodekool.codegen.model.tree.expression.Expression;

import java.util.*;

public class VariableDeclaration extends AbstractStatement implements VariableTree, WithMetaData {

    private Expression varType;

    private String name;

    private Expression initExpression;

    private AbstractSymbol symbol;

    private final List<AnnotationExpression> annotations = new ArrayList<>();

    private final Map<String, Object> metaData = new HashMap<>();

    private ElementKind kind;

    private final Set<Modifier> modifiers = new LinkedHashSet<>();

    public VariableDeclaration(final Expression varType,
                               final String name,
                               final Expression initExpression,
                               final AbstractSymbol symbol) {
        this.varType = varType;
        this.name = name;
        this.initExpression = initExpression;
        this.symbol = symbol;
    }

    public VariableDeclaration() {
    }

    public static VariableDeclaration parameter() {
        return new VariableDeclaration().kind(ElementKind.PARAMETER);
    }

    public ElementKind getKind() {
        return kind;
    }

    public VariableDeclaration kind(final ElementKind kind) {
        this.kind = kind;
        return this;
    }

    public Map<String, Object> getMetaData() {
        return metaData;
    }

    public Set<Modifier> getModifiers() {
        return modifiers;
    }

    public VariableDeclaration modifier(final Modifier modifier) {
        this.modifiers.add(modifier);
        return this;
    }

    public VariableDeclaration modifiers(final Modifier... modifiers) {
        for (final Modifier modifier : modifiers) {
            modifier(modifier);
        }
        return this;
    }

    public VariableDeclaration modifiers(final Collection<Modifier> modifiers) {
        for (final Modifier modifier : modifiers) {
            modifier(modifier);
        }
        return this;
    }

    public VariableDeclaration removeModifier(final Modifier modifier) {
        this.modifiers.remove(modifier);
        return this;
    }

    public boolean hasModifier(final Modifier modifier) {
        return this.modifiers.contains(modifier);
    }

    public Expression getVarType() {
        return varType;
    }

    public VariableDeclaration varType(final Expression varType) {
        this.varType = varType;
        return this;
    }

    public String getName() {
        return name;
    }

    public VariableDeclaration name(final String name) {
        this.name = name;
        return this;
    }

    public Optional<Expression> getInitExpression() {
        return Optional.ofNullable(initExpression);
    }

    public VariableDeclaration initExpression(final Expression initExpression) {
        this.initExpression = initExpression;
        return this;
    }

    public AbstractSymbol getSymbol() {
        return symbol;
    }

    public VariableDeclaration symbol(final AbstractSymbol symbol) {
        this.symbol = symbol;
        return this;
    }

    public VariableDeclaration annotation(final String className) {
        annotation(new AnnotationExpression(className));
        return this;
    }
    public VariableDeclaration annotation(final AnnotationExpression annotationExpression) {
        this.annotations.add(annotationExpression);
        return this;
    }

    public List<AnnotationExpression> getAnnotations() {
        return annotations;
    }

    public boolean isAnnotationPresent(final String name) {
        throw new UnsupportedOperationException();
    }


    @Override
    public <R, P> R accept(final TreeVisitor<R, P> visitor, final P param) {
        return visitor.visitVariableDeclaration(this, param);
    }

}
