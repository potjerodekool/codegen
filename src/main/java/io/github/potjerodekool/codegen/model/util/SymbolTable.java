package io.github.potjerodekool.codegen.model.util;

import io.github.potjerodekool.codegen.model.element.*;
import io.github.potjerodekool.codegen.model.symbol.AbstractSymbol;
import io.github.potjerodekool.codegen.model.symbol.ClassSymbol;
import io.github.potjerodekool.codegen.model.symbol.PackageSymbol;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {

    private final Map<Name, PackageSymbol> packages = new HashMap<>();

    private final Map<Name, ClassSymbol> classes = new HashMap<>();

    public boolean addClass(final ClassSymbol classSymbol) {
        return add(classSymbol, classes);
    }

    public boolean addPackage(final PackageSymbol packageSymbol) {
        return add(packageSymbol, packages);
    }

    private <T extends AbstractSymbol<T>> boolean add(final T symbol,
                                                      final Map<Name, T> symbols) {
        final Name name = getBinaryName(symbol);

        if (symbols.containsKey(name)) {
            return false;
        }
        symbols.put(name, symbol);
        return true;
    }

    public ClassSymbol findClass(final Name name) {
        return findSymbol(name, classes);
    }

    public PackageSymbol findPackage(final Name name) {
        return findSymbol(name, packages);
    }

    private  <T extends AbstractSymbol<T>> T findSymbol(final Name name,
                                                        final Map<Name, T> symbols) {
        final var internalName = name.toString().replace('.', '/');
        return symbols.get(Name.of(internalName));
    }

    public PackageSymbol findOrCreatePackageSymbol(final Name name) {
        var packageSymbol = findPackage(name);

        if (packageSymbol == null) {
            packageSymbol = PackageSymbol.create(Name.of(name));
            addPackage(packageSymbol);
        }
        return packageSymbol;
    }

    public ClassSymbol enterClass(final ElementKind kind,
                                  final Name simpleName,
                                  final NestingKind nestingKind,
                                  final Element enclosingElement) {
        final Name qualifiedName;

        if (enclosingElement == null) {
            qualifiedName = simpleName;
        } else {
            qualifiedName = Name.of(getQualifiedName(enclosingElement), simpleName);
        }

        var classSymbol = findClass(qualifiedName);

        if (classSymbol != null) {
            return classSymbol;
        } else {
            classSymbol = ClassSymbol.create(kind, simpleName, nestingKind, enclosingElement);
            addClass(classSymbol);
            return classSymbol;
        }
    }

    public Name getBinaryName(final Element type) {
        final var binaryNameBuilder = new StringBuilder();
        resolveBinaryName(type, binaryNameBuilder);
        return Name.of(binaryNameBuilder.toString());
    }

    public void resolveBinaryName(final Element element,
                                  final StringBuilder binaryNameBuilder) {
        final var enclosingElement = element.getEnclosingElement();

        if (enclosingElement != null && !isDefaultPackage(enclosingElement)) {
            resolveBinaryName(enclosingElement, binaryNameBuilder);

            if (element instanceof TypeElement typeElement && typeElement.getNestingKind() == NestingKind.MEMBER) {
                binaryNameBuilder.append("$");
            } else {
                binaryNameBuilder.append("/");
            }
        }

        if (element instanceof PackageSymbol packageSymbol) {
            binaryNameBuilder.append(packageSymbol.getQualifiedName().toString().replace('.', '/'));
        } else {
            binaryNameBuilder.append(element.getSimpleName());
        }
    }

    private boolean isDefaultPackage(final Element element) {
        return element instanceof PackageElement packageElement
                && packageElement.isUnnamed();
    }

    public Name getQualifiedName(final Element element) {
        if (element instanceof QualifiedNameable qualifiedNameable) {
            return qualifiedNameable.getQualifiedName();
        } else {
            return element.getSimpleName();
        }
    }
}
