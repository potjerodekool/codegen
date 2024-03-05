package io.github.potjerodekool.codegen.template.model;

public final class StarImportItem implements ImportItem {

    private final String name;

    public StarImportItem(final String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isImportFor(final String importName) {
        final var sepIndex = importName.lastIndexOf('.');

        if (sepIndex > -1) {
            final String packageName = importName.substring(0, sepIndex);
            return name.equals(packageName);
        }

        return false;
    }

    @Override
    public String toString() {
        return name + ".*";
    }
}
