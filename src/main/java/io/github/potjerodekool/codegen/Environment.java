package io.github.potjerodekool.codegen;

import io.github.potjerodekool.codegen.io.FileManager;
import io.github.potjerodekool.codegen.io.FileManagerImpl;
import io.github.potjerodekool.codegen.io.Filer;
import io.github.potjerodekool.codegen.io.FilerImpl;
import io.github.potjerodekool.codegen.loader.asm.AsmTypeElementLoader;
import io.github.potjerodekool.codegen.loader.kotlin.KotlinTypeElementLoader;
import io.github.potjerodekool.codegen.model.util.Elements;
import io.github.potjerodekool.codegen.model.util.ElementsImpl;
import io.github.potjerodekool.codegen.model.util.SymbolTable;
import io.github.potjerodekool.codegen.model.util.type.JavaTypes;
import io.github.potjerodekool.codegen.model.util.type.KotlinTypes;
import io.github.potjerodekool.codegen.model.util.type.Types;

import java.net.URL;

public class Environment {

    private final SymbolTable symbolTable;

    private final Elements elements;

    private final KotlinTypes kotlinTypes;

    private final FileManager fileManager;

    private final Filer filer;

    public Environment(final URL[] classPath) {
        this.symbolTable = new SymbolTable();
        final var javaTypeElementLoader = new AsmTypeElementLoader(classPath, symbolTable);
        final var kotlinTypeElementLoader = new KotlinTypeElementLoader(classPath, javaTypeElementLoader, symbolTable);
        this.elements = new ElementsImpl(kotlinTypeElementLoader, symbolTable);
        final var javaTypes = new JavaTypes(kotlinTypeElementLoader);
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
}
