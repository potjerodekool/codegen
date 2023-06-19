package io.github.potjerodekool.codegen.loader.kotlin;

import io.github.potjerodekool.codegen.loader.asm.AsmTypeElementLoader;
import io.github.potjerodekool.codegen.model.element.Name;
import io.github.potjerodekool.codegen.model.type.DeclaredType;
import io.github.potjerodekool.codegen.model.util.SymbolTable;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KotlinTypeElementLoaderTest {

    @Test
    void test() throws MalformedURLException {
        final var symbolTable = new SymbolTable();

        final var name = com.fasterxml.jackson.module.kotlin.KotlinModule.Builder.class.getName();

        final var file = new File("C:\\Users\\evert\\.m2\\repository\\org\\jetbrains\\kotlin\\kotlin-stdlib\\1.7.22\\kotlin-stdlib-1.7.22.jar");
        //final var file = new File("C:\\Users\\evert\\.m2\\repository\\com\\fasterxml\\jackson\\module\\jackson-module-kotlin\\2.14.0\\jackson-module-kotlin-2.14.0.jar");
        final var classPath = new URL[]{file.toURI().toURL()};
        final var asmTypeElementLoader = new AsmTypeElementLoader(classPath, symbolTable);
        final var loader = new KotlinTypeElementLoader(classPath, asmTypeElementLoader, symbolTable);
        final var element = loader.loadTypeElement("kotlin.collections.MutableList");

        assertEquals(Name.of("kotlin.collections.MutableList"), element.getQualifiedName());

        final var declaredType = (DeclaredType) element.asType();
        assertEquals(1, declaredType.getTypeArguments().size());
    }

}