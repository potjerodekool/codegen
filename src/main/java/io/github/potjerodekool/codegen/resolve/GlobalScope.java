package io.github.potjerodekool.codegen.resolve;

import io.github.potjerodekool.codegen.model.CompilationUnit;
import io.github.potjerodekool.codegen.model.element.Name;
import io.github.potjerodekool.codegen.model.symbol.AbstractSymbol;
import io.github.potjerodekool.codegen.model.util.Elements;
import io.github.potjerodekool.codegen.model.util.QualifiedName;

import java.util.Optional;

public class GlobalScope implements Scope {

    private final Elements elements;
    private final CompilationUnit compilationUnit;

    public GlobalScope(final Elements elements,
                       final CompilationUnit compilationUnit) {
        this.elements = elements;
        this.compilationUnit = compilationUnit;
    }

    public CompilationUnit getCompilationUnit() {
        return compilationUnit;
    }

    @Override
    public GlobalScope findGlobalScope() {
        return this;
    }

    @Override
    public Scope child() {
        return new DefaultScope(this);
    }

    @Override
    public Optional<AbstractSymbol<?>> resolveSymbol(final Name name) {
        if (name.toString().contains(".")) {
            return Optional.ofNullable((AbstractSymbol<?>) elements.getTypeElement(name));
        } else {
            final var imports = compilationUnit.getImports();
            final var simpleName = QualifiedName.from(name.toString()).simpleName();
            final var simpleNameWithDot = "." + simpleName;

            for (final var anImport : imports) {
                if (name.equals(anImport)) {
                    return Optional.ofNullable((AbstractSymbol<?>) elements.getTypeElement(name));
                } else if (anImport.toString().endsWith(simpleNameWithDot)) {
                    return Optional.ofNullable((AbstractSymbol<?>) elements.getTypeElement(anImport));
                }
            }

            return Optional.ofNullable((AbstractSymbol<?>) elements.getTypeElement(name));
        }
    }
}
