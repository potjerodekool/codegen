package io.github.potjerodekool.codegen.template.model;

public sealed interface ImportItem permits QualifiedImportItem, StarImportItem {
    boolean isImportFor(String importName);

    String getName();
}
