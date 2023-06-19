package io.github.potjerodekool.codegen.model.symbol;

import io.github.potjerodekool.codegen.model.element.*;
import io.github.potjerodekool.codegen.model.type.PackageType;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

public class PackageSymbol extends TypeSymbol<PackageSymbol> implements PackageElement {

    public static final PackageSymbol DEFAULT_PACKAGE = new PackageSymbol(Name.EMPTY);

    private PackageSymbol(final Name name) {
        super(ElementKind.PACKAGE, name, List.of());
    }

    public static PackageSymbol create(final @Nullable Name name) {
        if (name == null || name.isEmpty()) {
            return DEFAULT_PACKAGE;
        }

        final var pe = new PackageSymbol(name);
        final var type = new PackageType(pe);
        pe.setType(type);
        return pe;
    }

    @Override
    protected void validateSimpleName(final Name simpleName) {
    }

    @Override
    public Name getQualifiedName() {
        return getSimpleName();
    }

    @Override
    public <R, P> R accept(final ElementVisitor<R, P> v, final P p) {
        return v.visitPackage(this, p);
    }

    public boolean isDefaultPackage() {
        return this == DEFAULT_PACKAGE;
    }

    @Override
    public boolean isUnnamed() {
        return this == DEFAULT_PACKAGE;
    }
}
