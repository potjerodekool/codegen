package io.github.potjerodekool.codegen.io;

import io.github.potjerodekool.codegen.model.CompilationUnit;
import io.github.potjerodekool.codegen.Language;

import java.io.IOException;

public interface Filer {

    void writeSource(CompilationUnit compilationUnit,
                     Language language) throws IOException;

    FileObject getResource(Location location,
                           CharSequence moduleAndPkg,
                           String relativeName);

    FileObject createResource(Location location,
                              CharSequence moduleAndPkg,
                              String relativeName);
}
