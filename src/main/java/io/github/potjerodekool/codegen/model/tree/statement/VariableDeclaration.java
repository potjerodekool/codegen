package io.github.potjerodekool.codegen.model.tree.statement;

import io.github.potjerodekool.codegen.model.element.ElementKind;
import io.github.potjerodekool.codegen.model.element.Modifier;
import io.github.potjerodekool.codegen.model.symbol.AbstractSymbol;
import io.github.potjerodekool.codegen.model.tree.AnnotationExpression;
import io.github.potjerodekool.codegen.model.tree.TreeVisitor;
import io.github.potjerodekool.codegen.model.tree.WithMetaData;
import io.github.potjerodekool.codegen.model.tree.expression.Expression;

import java.util.*;

public abstract class VariableDeclaration<VD extends VariableDeclaration<VD>> extends AbstractStatement implements VariableTree, WithMetaData {

    private Expression varType;

    private String name;

    private Expression initExpression;

    private AbstractSymbol symbol;

    private final List<AnnotationExpression> annotations = new ArrayList<>();

    private final Map<String, Object> metaData = new HashMap<>();

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

    public abstract ElementKind getKind();

    public Map<String, Object> getMetaData() {
        return metaData;
    }

    public abstract Set<Modifier> getModifiers();

    public Expression getVarType() {
        return varType;
    }

    public VD varType(final Expression varType) {
        this.varType = varType;
        return (VD) this;
    }

    public String getName() {
        return name;
    }

    public VD name(final String name) {
        this.name = name;
        return (VD) this;
    }

    public Optional<Expression> getInitExpression() {
        return Optional.ofNullable(initExpression);
    }

    public VD initExpression(final Expression initExpression) {
        this.initExpression = initExpression;
        return (VD) this;
    }

    public AbstractSymbol getSymbol() {
        return symbol;
    }

    public void setSymbol(final AbstractSymbol symbol) {
        this.symbol = symbol;
    }

    public VD annotation(final String className) {
        annotation(new AnnotationExpression(className));
        return (VD) this;
    }
    public VD annotation(final AnnotationExpression annotationExpression) {
        this.annotations.add(annotationExpression);
        return (VD) this;
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
