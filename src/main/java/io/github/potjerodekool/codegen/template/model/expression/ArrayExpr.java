package io.github.potjerodekool.codegen.template.model.expression;

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

    public ArrayExpr withComponentType(final TypeExpr componentType) {
        this.componentType = componentType;
        return this;
    }

    public List<Expr> getValues() {
        return values;
    }

    public ArrayExpr withValue(final Expr value) {
        this.values.add(value);
        return this;
    }

    public ArrayExpr withValues(final Expr... values) {
        this.values.addAll(List.of(values));
        return this;
    }

    public ArrayExpr withValues(final List<Expr> values) {
        this.values.addAll(values);
        return this;
    }

    @Override
    public <P, R> R accept(final ExpressionVisitor<P, R> visitor, final P p) {
        return visitor.visitArrayExpression(this, p);
    }
}
