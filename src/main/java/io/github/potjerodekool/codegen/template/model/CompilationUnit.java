package io.github.potjerodekool.codegen.template.model;

import io.github.potjerodekool.codegen.Language;
import io.github.potjerodekool.codegen.template.model.element.TypeElem;

import java.util.ArrayList;
import java.util.List;

public class CompilationUnit {

    private final Language language;

    private String packageName;

    private final List<ImportItem> imports = new ArrayList<>();

    private final List<TypeElem> elements = new ArrayList<>();

    public CompilationUnit(final Language language) {
        this.language = language;
    }

    public Language getLanguage() {
        return language;
    }

    public String getPackageName() {
        return packageName;
    }

    public CompilationUnit withPackageName(final String packageName) {
        this.packageName = packageName;
        return this;
    }

    public List<ImportItem> getImports() {
        return imports;
    }

    public CompilationUnit withImport(final String importName) {
        if (importName.endsWith(".*")) {
            this.imports.add(new StarImportItem(importName.substring(0, importName.length() - 2)));
        } else {
            this.imports.add(new QualifiedImportItem(importName));
        }
        return this;
    }

    public List<TypeElem> getElements() {
        return elements;
    }

    public void withElement(final TypeElem element) {
        this.elements.add(element);
    }
}
