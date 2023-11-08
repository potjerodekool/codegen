package io.github.potjerodekool.codegen.model.symbol.kotlin;

import io.github.potjerodekool.codegen.model.element.ElementKind;
import io.github.potjerodekool.codegen.model.element.Modifier;
import io.github.potjerodekool.codegen.model.symbol.AbstractClassBuilder;
import io.github.potjerodekool.codegen.model.symbol.ClassSymbol;
import io.github.potjerodekool.codegen.model.type.ClassType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KClassSymbolBuilder extends AbstractClassBuilder<KClassSymbolBuilder, ClassSymbol> {

    private ElementKind kind;
    private final Set<Modifier> modifiers = new HashSet<>();

    public KClassSymbolBuilder kind(final ElementKind kind) {
        this.kind = kind;
        return this;
    }

    public KClassSymbolBuilder modifier(final Modifier modifier) {
        this.modifiers.add(modifier);
        return this;
    }

    public KClassSymbolBuilder modifiers(final Modifier... modifiers) {
        this.modifiers.addAll(List.of(modifiers));
        return this;
    }

    @Override
    public ClassSymbol build() {
        final var classSymbol = new ClassSymbol(kind, simpleName, nestingKind, enclosingElement);
        classSymbol.addModifiers(modifiers);
        final var classType = new ClassType(classSymbol, false);
        classSymbol.setType(classType);
        return classSymbol;
    }
}
