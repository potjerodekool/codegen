package io.github.potjerodekool.codegen.model.tree.statement;

import io.github.potjerodekool.codegen.model.element.ElementKind;
import io.github.potjerodekool.codegen.model.element.Modifier;
import io.github.potjerodekool.codegen.model.tree.AnnotationExpression;
import io.github.potjerodekool.codegen.model.tree.TreeVisitor;
import io.github.potjerodekool.codegen.model.tree.expression.Expression;
import io.github.potjerodekool.codegen.model.symbol.AbstractSymbol;
import io.github.potjerodekool.codegen.model.tree.expression.NameExpression;
import io.github.potjerodekool.codegen.model.tree.type.ParameterizedType;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;

public class VariableDeclaration extends AbstractStatement implements VariableTree {

    private final ElementKind kind;

    private final Set<Modifier> modifiers;

    private Expression varType;

    private String name;

    private final @Nullable Expression initExpression;

    private AbstractSymbol<?> symbol;

    private final List<AnnotationExpression> annotations = new ArrayList<>();

    private final Map<String, Object> metaData = new HashMap<>();

    public VariableDeclaration(final ElementKind kind,
                               final Set<Modifier> modifiers,
                               final Expression varType,
                               final String name,
                               final @Nullable Expression initExpression,
                               final AbstractSymbol<?> symbol) {
        this.kind = kind;
        this.modifiers = modifiers;
        this.varType = varType;
        this.name = name;
        this.initExpression = initExpression;
        this.symbol = symbol;
    }

    public ElementKind getKind() {
        return kind;
    }

    public Map<String, Object> getMetaData() {
        return metaData;
    }

    public Set<Modifier> getModifiers() {
        return modifiers;
    }

    public Expression getVarType() {
        return varType;
    }

    public void setVarType(final Expression varType) {
        this.varType = varType;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Optional<Expression> getInitExpression() {
        return Optional.ofNullable(initExpression);
    }

    public AbstractSymbol<?> getSymbol() {
        return symbol;
    }

    public void setSymbol(final AbstractSymbol<?> symbol) {
        this.symbol = symbol;
    }

    public void addAnnotation(final String className) {
        addAnnotation(new AnnotationExpression(new ParameterizedType(new NameExpression(className))));
    }

    public void addAnnotation(final AnnotationExpression annotationExpression) {
        this.annotations.add(annotationExpression);
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
