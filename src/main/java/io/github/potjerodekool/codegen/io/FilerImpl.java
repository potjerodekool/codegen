package io.github.potjerodekool.codegen.io;

import io.github.potjerodekool.codegen.*;
import io.github.potjerodekool.codegen.java.JavaAstPrinter;
import io.github.potjerodekool.codegen.kotlin.JavaToKotlinConverter;
import io.github.potjerodekool.codegen.kotlin.KotlinAstPrinter;
import io.github.potjerodekool.codegen.model.CompilationUnit;
import io.github.potjerodekool.codegen.model.symbol.PackageSymbol;
import io.github.potjerodekool.codegen.model.symbol.ClassSymbol;
import io.github.potjerodekool.codegen.model.tree.statement.ClassDeclaration;
import io.github.potjerodekool.codegen.model.util.Elements;
import io.github.potjerodekool.codegen.model.util.type.Types;
import io.github.potjerodekool.codegen.resolve.ImportOrganiser;
import io.github.potjerodekool.codegen.resolve.SymbolResolver;

import java.io.IOException;

public class FilerImpl implements Filer {

    private final Elements elements;
    private final Types types;
    private final FileManager fileManager;

    public FilerImpl(final Elements elements,
                     final Types types,
                     final FileManager fileManager) {
        this.elements = elements;
        this.types = types;
        this.fileManager = fileManager;
    }

    @Override
    public void writeSource(final CompilationUnit compilationUnit,
                            final Language language) throws IOException {
        if (compilationUnit.getElements().size() > 0) {
            doWriteSourceOld(compilationUnit, language);
        } else {
            doWriteSourceNew(compilationUnit, language);
        }
    }

    private void doWriteSourceNew(final CompilationUnit compilationUnit,
                                  final Language language) throws IOException {
        final var clazz = (ClassDeclaration) compilationUnit.getClassDeclarations().get(0);
        final var packageElement = (PackageSymbol) compilationUnit.getPackageElement();
        final var packageName = packageElement != null
                ? packageElement.getQualifiedName()
                : "";

        final var fileObject = createResource(
                Location.SOURCE_OUTPUT,
                packageName,
                clazz.getSimpleName() + "." + language.getFileExtension()
        );

        try (final var writer = fileObject.openWriter()) {
            final var printer = Printer.create(writer);
            final CompilationUnit cu;
            final AbstractAstPrinter astPrinter;

            if (language == Language.KOTLIN) {
                cu = new JavaToKotlinConverter(elements, types).convert(compilationUnit);
                astPrinter = new KotlinAstPrinter(printer, types);
            } else {
                cu = compilationUnit;
                astPrinter = new JavaAstPrinter(printer, types);
            }

            resolve(cu);
            cu.accept(astPrinter, new CodeContext(cu));
            writer.flush();
        }
    }

    private void doWriteSourceOld(final CompilationUnit compilationUnit,
                                  final Language language) throws IOException {
        final var clazz = (ClassSymbol) compilationUnit.getElements().get(0);
        final var packageElement = (PackageSymbol) clazz.getEnclosingElement();
        final var packageName = packageElement != null
                ? packageElement.getQualifiedName()
                : "";

        final var fileObject = createResource(
                Location.SOURCE_OUTPUT,
                packageName,
                clazz.getSimpleName() + "." + language.getFileExtension()
        );

        try (final var writer = fileObject.openWriter()) {
            final var printer = Printer.create(writer);
            final CompilationUnit cu;
            final AbstractAstPrinter astPrinter;

            if (language == Language.KOTLIN) {
                cu = new JavaToKotlinConverter(elements, types).convert(compilationUnit);
                astPrinter = new KotlinAstPrinter(printer, types);
            } else {
                cu = compilationUnit;
                astPrinter = new JavaAstPrinter(printer, types);
            }

            resolve(cu);
            cu.accept(astPrinter, new CodeContext(cu));
            writer.flush();
        }
    }

    private void resolve(final CompilationUnit cu) {
        ///new SymbolResolver(elements, types).resolveSymbols(cu);
        new ImportOrganiser().organiseImports(cu);
    }

    @Override
    public FileObject getResource(final Location location,
                                  final CharSequence moduleAndPkg,
                                  final String relativeName) {
        return fileManager.getResource(location, moduleAndPkg, relativeName);
    }

    @Override
    public FileObject createResource(final Location location,
                                     final CharSequence moduleAndPkg,
                                     final String relativeName) {
        return fileManager.createResource(location, moduleAndPkg, relativeName);
    }
}
