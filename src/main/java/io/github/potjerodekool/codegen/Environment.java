package io.github.potjerodekool.codegen;

import io.github.potjerodekool.codegen.io.FileManager;
import io.github.potjerodekool.codegen.io.FileManagerImpl;
import io.github.potjerodekool.codegen.io.Filer;
import io.github.potjerodekool.codegen.io.FilerImpl;
import io.github.potjerodekool.codegen.loader.java.JavaElements;
import io.github.potjerodekool.codegen.loader.kotlin.KotlinElements;
import io.github.potjerodekool.codegen.model.CompilationUnit;
import io.github.potjerodekool.codegen.model.util.Elements;
import io.github.potjerodekool.codegen.model.util.SymbolTable;
import io.github.potjerodekool.codegen.model.util.type.JavaTypes;
import io.github.potjerodekool.codegen.model.util.type.KotlinTypes;
import io.github.potjerodekool.codegen.model.util.type.Types;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Environment {

    private final SymbolTable symbolTable;

    private final Elements elements;

    private final KotlinTypes kotlinTypes;

    private final FileManager fileManager;

    private final Filer filer;

    private final List<CompilationUnit> compilationUnits = new ArrayList<>();

    public Environment(final URL[] classPath) {
        this.symbolTable = new SymbolTable();
        final var javaTypes = new JavaTypes(symbolTable);
        final var javaElements = new JavaElements(symbolTable, classPath, javaTypes);
        this.elements = new KotlinElements(symbolTable, classPath, javaElements);
        this.kotlinTypes = new KotlinTypes(javaTypes);
        this.fileManager = new FileManagerImpl();
        this.filer = new FilerImpl(elements, kotlinTypes, fileManager);
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public Elements getElementUtils() {
        return elements;
    }

    public Types getTypes() {
        return kotlinTypes;
    }

    public Filer getFiler() {
        return filer;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public List<CompilationUnit> getCompilationUnits() {
        return compilationUnits;
    }
}
