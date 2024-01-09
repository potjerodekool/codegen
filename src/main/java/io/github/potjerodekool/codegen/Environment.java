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

    private final JavaElements javaElements;

    private final KotlinElements kotlinElements;

    private final JavaTypes javaTypes;

    private final KotlinTypes kotlinTypes;

    private final FileManager fileManager;

    private final Filer filer;

    private final List<CompilationUnit> compilationUnits = new ArrayList<>();

    public Environment(final URL[] classPath) {
        this.symbolTable = new SymbolTable();
        this.javaTypes = new JavaTypes(symbolTable);
        this.javaElements = new JavaElements(symbolTable, classPath, javaTypes);
        this.kotlinElements = new KotlinElements(symbolTable, classPath, javaElements);
        this.kotlinTypes = new KotlinTypes(javaTypes);
        this.fileManager = new FileManagerImpl();
        this.filer = new FilerImpl(kotlinElements, kotlinTypes, fileManager);
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public JavaElements getJavaElements() {
        return javaElements;
    }

    public JavaTypes getJavaTypes() {
        return javaTypes;
    }

    public KotlinElements getKotlinElements() {
        return kotlinElements;
    }

    public KotlinTypes getKotlinTypes() {
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
