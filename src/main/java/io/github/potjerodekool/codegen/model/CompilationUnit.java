package io.github.potjerodekool.codegen.model;

import io.github.potjerodekool.codegen.Language;
import io.github.potjerodekool.codegen.io.FileObject;
import io.github.potjerodekool.codegen.model.element.*;
import io.github.potjerodekool.codegen.model.symbol.PackageSymbol;
import io.github.potjerodekool.codegen.model.tree.PackageDeclaration;
import io.github.potjerodekool.codegen.model.tree.Tree;
import io.github.potjerodekool.codegen.model.tree.statement.ClassDeclaration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompilationUnit implements AstNode {

    private FileObject fileObject;

    private PackageSymbol packageElement = PackageSymbol.DEFAULT_PACKAGE;

    private final List<Name> imports = new ArrayList<>();

    private final List<Element> elements = new ArrayList<>();

    private final List<Tree> definitions = new ArrayList<>();

    private final Language language;

    public CompilationUnit(final Language language) {
        this.language = language;
    }

    public FileObject getFileObject() {
        return fileObject;
    }

    public void setFileObject(final FileObject fileObject) {
        this.fileObject = fileObject;
    }

    public Language getLanguage() {
        return language;
    }

    public PackageSymbol getPackageElement() {
        return packageElement;
    }

    public void setPackageElement(final PackageSymbol packageElement) {
        this.packageElement = packageElement;
    }

    public List<Name> getImports() {
        return Collections.unmodifiableList(imports);
    }

    public void addImport(final Name name) {
        this.imports.add(name);
    }

    public List<Element> getElements() {
        return Collections.unmodifiableList(elements);
    }

    public void addElement(final Element element) {
        this.elements.add(element);
    }

    public void removeElement(final Element element) {
        this.elements.remove(element);
    }

    public List<Tree> getDefinitions() {
        return definitions;
    }

    public PackageDeclaration getPackageDeclaration() {
        final var definition = definitions.size() > 0 ? definitions.get(0) : null;
        return definition instanceof PackageDeclaration packageDeclaration
                ? packageDeclaration
                : null;
    }

    public List<ClassDeclaration> getClassDeclarations() {
        return definitions.stream()
                .filter(it -> it instanceof ClassDeclaration)
                .map(it -> (ClassDeclaration) it)
                .toList();
    }

    public void add(Tree definition) {
        this.definitions.add(definition);
    }

    public <R, P> R accept(final CompilationUnitVisitor<R, P> visitor,
                           final P param) {
        return visitor.visitCompilationUnit(this, param);
    }
}
