package io.github.potjerodekool.codegen.model.util;

import io.github.potjerodekool.codegen.model.util.type.JavaTypes;
import org.junit.jupiter.api.Test;

class ElementsImplTest {

    private final SymbolTable symbolTable = new SymbolTable();
    private final JavaTypes types = new JavaTypes(symbolTable);
    private final ElementsImpl elements = new ElementsImpl(symbolTable);

    @Test
    void getBinaryNameOfMember() {
        /*
        final var packageSymbol = PackageSymbol.create(Name.of("java.util"));


        final var mapClass = new JClassSymbol(
                JElementKind.INTERFACE,
                "Map",
                NestingKind.TOP_LEVEL,
                packageSymbol
        );

        final var entryClass =
                new JClassSymbol(
                        JElementKind.INTERFACE,
                        "Entry",
                        NestingKind.MEMBER,
                        mapClass
                );

        final var binaryName = elements.getBinaryName(entryClass);
        assertEquals(Name.of("java/util/Map$Entry"), binaryName);
         */
    }

    @Test
    void getBinaryNameOfMember2() {
        /*
        final var packageSymbol = PackageSymbol.create(Name.of("org.some"));
        final var firstClass = new JClassSymbol(
                        JElementKind.INTERFACE,
                        "First",
                        NestingKind.TOP_LEVEL,
                        packageSymbol
                );

        final var secondClass = new JClassSymbol(
                JElementKind.INTERFACE,
                "Second",
                NestingKind.MEMBER,
                firstClass
        );

        final var thirdClass = new JClassSymbol(
                JElementKind.INTERFACE,
                "Third",
                NestingKind.MEMBER,
                secondClass
        );

        final var binaryName = elements.getBinaryName(thirdClass);
        assertEquals(Name.of("org/some/First$Second$Third"), binaryName);
         */
    }
}