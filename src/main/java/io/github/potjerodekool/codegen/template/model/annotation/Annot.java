package io.github.potjerodekool.codegen.template.model.annotation;


import io.github.potjerodekool.codegen.template.model.expression.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Annot implements Expr {

    private String name;

    private final Map<String, Expr> attributes = new HashMap<>();

    private AnnotTarget target;

    public Annot() {
    }

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

    @Deprecated
    public Map<String, Expr> getValues() {
        return getAttributes();
    }

    public Map<String, Expr> getAttributes() {
        return attributes;
    }

    @Deprecated

    public Annot value(final Expr value) {
        return attribute("value", value);
    }

    public Annot attribute(final Expr value) {
        return attribute("value", value);
    }

    @Deprecated
    public <E extends Expr> Annot value(final List<E> values) {
        return attribute("value", new ArrayExpr().values(values));
    }

    public <E extends Expr> Annot attribute(final List<E> values) {
        return attribute("value", new ArrayExpr().values(values));
    }

    @Deprecated
    public Annot value(final String key,
                       final Expr value) {
        return attribute(key, value);
    }

    public Annot attribute(final String key,
                           final Expr value) {
        this.attributes.put(key, value);
        return this;
    }

    @Deprecated
    public Annot value(final String key,
                       final String value) {
        return attribute(key, value);
    }

    public Annot attribute(final String key,
                           final String value) {
        this.attributes.put(key, new SimpleLiteralExpr(value));
        return this;
    }

    @Deprecated
    public <E extends Expr> Annot value(final String key,
                                        final List<E> values) {
        return attribute(key, values);
    }

    public <E extends Expr> Annot attribute(final String key,
                                            final List<E> values) {
        this.attributes.put(key, new ArrayExpr().values(values));
        return this;
    }

    public AnnotTarget getTarget() {
        return target;
    }

    public Annot target(final AnnotTarget target) {
        this.target = target;
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

