package io.github.potjerodekool.codegen.model;

import io.github.potjerodekool.codegen.Language;
import io.github.potjerodekool.codegen.io.FileObject;
import io.github.potjerodekool.codegen.model.element.*;
import io.github.potjerodekool.codegen.model.tree.MethodDeclaration;
import io.github.potjerodekool.codegen.model.tree.PackageDeclaration;
import io.github.potjerodekool.codegen.model.tree.Tree;
import io.github.potjerodekool.codegen.model.tree.statement.ClassDeclaration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompilationUnit implements AstNode {

    private FileObject fileObject;

    private PackageDeclaration packageDeclaration;

    private final List<Name> imports = new ArrayList<>();

    private final List<Tree> definitions = new ArrayList<>();

    private final Language language;

    public ImportScope importScope;

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


    public List<Name> getImports() {
        return Collections.unmodifiableList(imports);
    }

    public void addImport(final Name name) {
        this.imports.add(name);
    }

    public List<Tree> getDefinitions() {
        return definitions;
    }

    public PackageDeclaration getPackageDeclaration() {
        return packageDeclaration;
    }

    public List<ClassDeclaration> getClassDeclarations() {
        return definitions.stream()
                .filter(it -> it instanceof ClassDeclaration)
                .map(it -> (ClassDeclaration) it)
                .toList();
    }

    public void remove(final Tree definition) {
        this.definitions.remove(definition);
    }

    public CompilationUnit packageDeclaration(final PackageDeclaration packageDeclaration) {
        this.packageDeclaration = packageDeclaration;
        return this;
    }

    public CompilationUnit classDeclaration(final ClassDeclaration classDeclaration) {
        this.definitions.add(classDeclaration);
        return this;
    }

    public CompilationUnit methodDeclaration(final MethodDeclaration methodDeclaration) {
        this.definitions.add(methodDeclaration);
        return this;
    }

    public <R, P> R accept(final CompilationUnitVisitor<R, P> visitor,
                           final P param) {
        return visitor.visitCompilationUnit(this, param);
    }
}
