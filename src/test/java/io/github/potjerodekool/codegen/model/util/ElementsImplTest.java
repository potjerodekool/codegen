package io.github.potjerodekool.codegen.model.util;

import io.github.potjerodekool.codegen.loader.TypeElementLoader;
import io.github.potjerodekool.codegen.loader.asm.AsmTypeElementLoader;
import io.github.potjerodekool.codegen.loader.asm.ClassPath;
import io.github.potjerodekool.codegen.model.element.ElementKind;
import io.github.potjerodekool.codegen.model.element.Name;
import io.github.potjerodekool.codegen.model.element.NestingKind;
import io.github.potjerodekool.codegen.model.symbol.ClassSymbol;
import io.github.potjerodekool.codegen.model.symbol.PackageSymbol;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ElementsImplTest {

    private final SymbolTable symbolTable = new SymbolTable();
    private final TypeElementLoader loader = new AsmTypeElementLoader(ClassPath.getJavaClassPath(), symbolTable);
    private final ElementsImpl elements = new ElementsImpl(loader, symbolTable);

    @Test
    void getBinaryNameOfMember() {
        final var packageSymbol = PackageSymbol.create(Name.of("java.util"));
        final var mapClass = ClassSymbol.create(ElementKind.INTERFACE, Name.of("Map"), NestingKind.TOP_LEVEL, packageSymbol);
        final var entryClass = ClassSymbol.create(ElementKind.INTERFACE, Name.of("Entry"), NestingKind.MEMBER, mapClass);

        final var binaryName = elements.getBinaryName(entryClass);
        assertEquals(Name.of("java.util.Map$Entry"), binaryName);
    }

    @Test
    void getBinaryNameOfMember2() {
        final var packageSymbol = PackageSymbol.create(Name.of("org.some"));
        final var firstClass = ClassSymbol.create(ElementKind.INTERFACE, Name.of("First"), NestingKind.TOP_LEVEL, packageSymbol);
        final var secondClass = ClassSymbol.create(ElementKind.INTERFACE, Name.of("Second"), NestingKind.MEMBER, firstClass);
        final var thirdClass = ClassSymbol.create(ElementKind.INTERFACE, Name.of("Third"), NestingKind.MEMBER, secondClass);

        final var binaryName = elements.getBinaryName(thirdClass);
        assertEquals(Name.of("org.some.First$Second$Third"), binaryName);
    }
}