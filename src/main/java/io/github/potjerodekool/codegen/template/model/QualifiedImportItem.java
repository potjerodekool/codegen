package io.github.potjerodekool.codegen.template.model;

public final class QualifiedImportItem implements ImportItem {

    private final String name;

    public QualifiedImportItem(final String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isImportFor(final String importName) {
        return name.equals(importName);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
