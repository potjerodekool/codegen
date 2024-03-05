package io.github.potjerodekool.codegen.template.model.annotation;


import io.github.potjerodekool.codegen.template.model.expression.ArrayExpr;
import io.github.potjerodekool.codegen.template.model.expression.Expr;
import io.github.potjerodekool.codegen.template.model.expression.ExpressionKind;
import io.github.potjerodekool.codegen.template.model.expression.ExpressionVisitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Annot implements Expr {

    private String name;

    private final Map<String, Expr> values = new HashMap<>();

    public Annot(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Annot name(final String name) {
        this.name = name;
        return this;
    }

    public Map<String, Expr> getValues() {
        return values;
    }


    public Annot value(final Expr value) {
        return value("value", value);
    }

    public <E extends Expr> Annot value(final List<E> values) {
        return value("value", new ArrayExpr().values(values));
    }

    public Annot value(final String key,
                       final Expr value) {
        this.values.put(key, value);
        return this;
    }

    public <E extends Expr> Annot value(final String key,
                                        final List<E> values) {
        this.values.put(key, new ArrayExpr().values(values));
        return this;
    }

    @Override
    public ExpressionKind getKind() {
        return ExpressionKind.ANNOTATION;
    }

    @Override
    public <P, R> R accept(final ExpressionVisitor<P, R> visitor, final P param) {
        return visitor.visitAnnotation(this, param);
    }
}

