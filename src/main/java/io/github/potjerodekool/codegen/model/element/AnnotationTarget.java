package io.github.potjerodekool.codegen.model.element;

public enum AnnotationTarget {
    FIELD("field");

    private final String prefix;

    AnnotationTarget(final String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}
