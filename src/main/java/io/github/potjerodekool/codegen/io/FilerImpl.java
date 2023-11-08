package io.github.potjerodekool.codegen.io;

import io.github.potjerodekool.codegen.*;
import io.github.potjerodekool.codegen.java.JavaAstPrinter;
import io.github.potjerodekool.codegen.kotlin.JavaToKotlinConverter;
import io.github.potjerodekool.codegen.kotlin.KotlinAstPrinter;
import io.github.potjerodekool.codegen.model.CompilationUnit;
import io.github.potjerodekool.codegen.model.tree.statement.ClassDeclaration;
import io.github.potjerodekool.codegen.model.util.Elements;
import io.github.potjerodekool.codegen.model.util.type.Types;
import io.github.potjerodekool.codegen.resolve.ImportOrganiser;

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
        doWriteSourceNew(compilationUnit, language);
    }

    private void doWriteSourceNew(final CompilationUnit compilationUnit,
                                  final Language language) throws IOException {
        final var clazz = (ClassDeclaration) compilationUnit.getClassDeclarations().get(0);
        final var packageDeclaration = compilationUnit.getPackageDeclaration();
        final var packageName = packageDeclaration != null
                ? packageDeclaration.getName().getName()
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

            if (language == Language.KOTLIN && compilationUnit.getLanguage() == Language.JAVA) {
                cu = new JavaToKotlinConverter(elements, types).convert(compilationUnit);
            } else {
                cu = compilationUnit;
            }

            if (language == Language.KOTLIN) {
                astPrinter = new KotlinAstPrinter(printer, types);
            } else {
                astPrinter = new JavaAstPrinter(printer, types);
            }

            resolve(cu);
            cu.accept(astPrinter, new CodeContext(cu));
            writer.flush();
        }
    }



    private void resolve(final CompilationUnit cu) {
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
