package io.github.potjerodekool.codegen.template.model.expression;

import io.github.potjerodekool.codegen.template.model.type.TypeExpr;

import java.util.ArrayList;
import java.util.List;

public class ArrayExpr implements Expr {

    private TypeExpr componentType;

    private final List<Expr> values = new ArrayList<>();

    @Override
    public ExpressionKind getKind() {
        return ExpressionKind.ARRAY;
    }

    public TypeExpr getComponentType() {
        return componentType;
    }

    public ArrayExpr componentType(final TypeExpr componentType) {
        this.componentType = componentType;
        return this;
    }

    public List<Expr> getValues() {
        return values;
    }

    public ArrayExpr value(final Expr value) {
        this.values.add(value);
        return this;
    }

    public ArrayExpr values(final Expr... values) {
        this.values.addAll(List.of(values));
        return this;
    }

    public ArrayExpr values(final List<Expr> values) {
        this.values.addAll(values);
        return this;
    }

    @Override
    public <P, R> R accept(final ExpressionVisitor<P, R> visitor, final P p) {
        return visitor.visitArrayExpression(this, p);
    }
}
