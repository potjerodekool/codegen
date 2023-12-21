package io.github.potjerodekool.codegen.template.model.annotation;


import io.github.potjerodekool.codegen.template.model.expression.Expr;

import java.util.HashMap;
import java.util.Map;

public class Annot {

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


    public Annot withValue(final Expr value) {
        values.put("value", value);
        return this;
    }

    public Annot withValue(final String key,
                           final Expr value) {
        values.put(key, value);
        return this;
    }

    public <P, R> R accept(final AnnotationVisitor<P,R> visitor, final P param) {
        return visitor.visitAnnotation(this, param);
    }
}

