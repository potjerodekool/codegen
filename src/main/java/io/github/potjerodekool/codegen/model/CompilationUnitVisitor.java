package io.github.potjerodekool.codegen.model;

import io.github.potjerodekool.codegen.model.CompilationUnit;

public interface CompilationUnitVisitor<R,P> {

    R visitCompilationUnit(CompilationUnit compilationUnit, P param);
}
