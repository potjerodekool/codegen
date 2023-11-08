package io.github.potjerodekool.codegen.loader.java;

import io.github.potjerodekool.codegen.loader.java.visitor.AsmClassVisitor;
import io.github.potjerodekool.codegen.model.element.Name;
import io.github.potjerodekool.codegen.model.element.TypeElement;
import io.github.potjerodekool.codegen.model.symbol.ClassSymbol;
import io.github.potjerodekool.codegen.model.util.AbstractElements;
import io.github.potjerodekool.codegen.model.util.SymbolTable;
import io.github.potjerodekool.codegen.model.util.type.Types;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.zip.ZipFile;

public class JavaElements extends AbstractElements {

    private final File[] classPath;
    private final Types types;

    public JavaElements(final SymbolTable symbolTable,
                        final URL[] classPath,
                        final Types types) {
        super(symbolTable);
        this.classPath = toFiles(classPath);
        this.types = types;
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
    protected TypeElement doLoadTypeElement(final CharSequence name) {
        final var bytecode = resolveClassByteCode(name);

        if (bytecode == null) {
            return null;
        }

        return doDefineClass(bytecode);
    }

    private byte[] resolveClassByteCode(final CharSequence name) {
        final var classFileName = name.toString().replace('.', '/');

        byte[] bytecode = null;
        final var fileName = classFileName + ".class";

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

    public ClassSymbol doDefineClass(final byte[] bytecode) {
        final var reader = new ClassReader(bytecode);
        final var visitor = new AsmClassVisitor(Opcodes.ASM9, getSymbolTable(), this, types);

        reader.accept(visitor, ClassReader.SKIP_CODE);
        return visitor.getClassSymbol();
    }

    @Override
    protected TypeElement findTypeElement(final CharSequence name) {
        final var internalName = toInternalName(name);
        return getSymbolTable().getClass(null, Name.of(internalName));
    }
}
