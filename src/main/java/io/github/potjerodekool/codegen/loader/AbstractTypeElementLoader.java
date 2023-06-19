package io.github.potjerodekool.codegen.loader;

import io.github.potjerodekool.codegen.model.element.ElementFilter;
import io.github.potjerodekool.codegen.model.element.Name;
import io.github.potjerodekool.codegen.model.symbol.ClassSymbol;
import io.github.potjerodekool.codegen.model.util.Elements;
import io.github.potjerodekool.codegen.model.util.SymbolTable;

public abstract class AbstractTypeElementLoader implements TypeElementLoader {

    private final SymbolTable symbolTable;

    protected AbstractTypeElementLoader(final SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    @Override
    public ClassSymbol loadTypeElement(final String name) {
        final var internalName = toInternalName(name);
        ClassSymbol classSymbol = findClass(internalName);

        if (classSymbol != null) {
            return classSymbol;
        }

        classSymbol = doLoadTypeElement(internalName);

        if (classSymbol != null) {
            return classSymbol;
        }

        final var sep = internalName.lastIndexOf('/');

        if (sep > -1) {
            final var parentName = internalName.substring(0, sep);
            final var parentSymbol = loadTypeElement(parentName);

            if (parentSymbol != null) {
                final var childName = internalName.substring(sep + 1);

                return ElementFilter.types(parentSymbol)
                        .filter(it -> it instanceof ClassSymbol)
                        .filter(it -> it.getSimpleName().contentEquals(childName))
                        .map(it -> (ClassSymbol) it)
                        .findFirst()
                        .orElse(null);
            }
        }

        return null;
    }

    private ClassSymbol findClass(final String internalName) {
        return symbolTable.findClass(Name.of(internalName));
    }

    protected String toInternalName(final String name) {
        return name.replace('.', '/');
    }

    protected SymbolTable getSymbolTable() {
        return symbolTable;
    }
}
