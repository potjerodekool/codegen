package io.github.potjerodekool.codegen.loader.asm;

import io.github.potjerodekool.codegen.loader.AbstractTypeElementLoader;
import io.github.potjerodekool.codegen.loader.asm.visitor.AsmClassVisitor;
import io.github.potjerodekool.codegen.model.symbol.ClassSymbol;
import io.github.potjerodekool.codegen.model.util.SymbolTable;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.zip.ZipFile;

public class AsmTypeElementLoader extends AbstractTypeElementLoader {

    private final File[] classPath;

    public AsmTypeElementLoader(final URL[] classPath, final SymbolTable symbolTable) {
        super(symbolTable);
        this.classPath = toFiles(classPath);
    }

    private static File[] toFiles(final URL[] classPath) {
        return Arrays.stream(classPath)
                .map(url -> {
                    try {
                        return new File(url.toURI());
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toArray(File[]::new);
    }

    @Override
    public ClassSymbol doLoadTypeElement(final String name) {
        final var bytecode = resolveClass(name);

        if (bytecode == null || bytecode.length == 0) {
            return null;
        }
        return doDefineClass(bytecode);
    }

    private ClassSymbol doDefineClass(final byte[] bytecode) {
        final var reader = new ClassReader(bytecode);
        final var visitor = new AsmClassVisitor(Opcodes.ASM9, this, null, getSymbolTable());
        reader.accept(visitor, ClassReader.SKIP_CODE);
        return visitor.getClassSymbol();
    }

    private byte[] resolveClass(final String internalName) {
        byte[] bytecode = null;
        final var fileName = internalName + ".class";

        for (final File file : classPath) {
            if (file.getName().endsWith(".jmod")) {
                bytecode = doResolveClassIn("classes/" + fileName, file);
            } else if (file.getName().endsWith(".jar")) {
                bytecode = doResolveClassIn(fileName, file);
            }

            if (bytecode != null) {
                return bytecode;
            }
        }
        return null;
    }

    private byte[] doResolveClassIn(final String fileName, final File file) {
        try(final var zipFile = new ZipFile(file)) {
            final var entry = zipFile.getEntry(fileName);

            if (entry != null) {
                final var inputStream = zipFile.getInputStream(entry);
                return inputStream.readAllBytes();
            }

        } catch (final IOException ignored) {
            //ignore
        }
        return null;
    }

}
