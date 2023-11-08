package io.github.potjerodekool.codegen.model.symbol;

import io.github.potjerodekool.codegen.model.element.*;
import io.github.potjerodekool.codegen.resolve.Scope;
import io.github.potjerodekool.codegen.resolve.WritableScope;

import java.util.List;
import java.util.Set;

public class PackageSymbol extends TypeSymbol implements PackageElement {

    public WritableScope scope;

    public ModuleSymbol module;

    public PackageSymbol(final Name name,
                         final PackageSymbol enclosingPackage) {
        super(ElementKind.PACKAGE, name, List.of());
        setEnclosingElement(enclosingPackage);
    }

    @Override
    public ElementKind getKind() {
        return ElementKind.PACKAGE;
    }

    @Override
    protected void validateSimpleName(final CharSequence simpleName) {
    }

    @Override
    public Name getQualifiedName() {
        final var enclosing = (PackageSymbol) getEnclosingElement();

        if (enclosing == null) {
            return getSimpleName();
        } else {
            final var enclosingName = enclosing.getQualifiedName();
            return enclosingName.append(getSimpleName());
        }
    }

    @Override
    public <R, P> R accept(final ElementVisitor<R, P> v, final P p) {
        return v.visitPackage(this, p);
    }

    public boolean isDefaultPackage() {
        return getEnclosingElement() != null
                && getSimpleName().contentEquals("");
    }

    @Override
    public boolean isUnnamed() {
        return isDefaultPackage();
    }

    @Override
    public Set<? extends Modifier> getModifiers() {
        return Set.of();
    }

    @Override
    public WritableScope members() {
        return scope;
    }
}
