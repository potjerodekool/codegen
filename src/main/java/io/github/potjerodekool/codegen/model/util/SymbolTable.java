package io.github.potjerodekool.codegen.model.util;

import io.github.potjerodekool.codegen.model.element.*;
import io.github.potjerodekool.codegen.model.symbol.AbstractSymbol;
import io.github.potjerodekool.codegen.model.symbol.ClassSymbol;
import io.github.potjerodekool.codegen.model.symbol.PackageSymbol;
import io.github.potjerodekool.codegen.model.symbol.ModuleSymbol;
import io.github.potjerodekool.codegen.model.type.ClassType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SymbolTable {

    private final Map<Name, Map<ModuleSymbol, PackageSymbol>> packages = new HashMap<>();

    private final Map<Name, Map<ModuleElement, ClassSymbol>> classes = new HashMap<>();

    private final ModuleSymbol NO_MODULE = new ModuleSymbol(Name.of(""));

    private final NameResolver nameResolver = new InternalNameResolver();

    public PackageSymbol enterPackage(final ModuleSymbol moduleElement,
                                      final Name fullName) {
        var packageSymbol = getPackage(moduleElement, fullName);

        if (packageSymbol != null) {
            return packageSymbol;
        }

        final var packagePart = fullName.packagePart();
        final PackageSymbol enclosingPackage;

        if (!packagePart.isEmpty()) {
            enclosingPackage = enterPackage(moduleElement, packagePart);

        } else {
            enclosingPackage = null;
        }

        packageSymbol = new PackageSymbol(
                fullName.shortName(),
                enclosingPackage
        );

        doEnterPackage(moduleElement, packageSymbol);
        return packageSymbol;
    }

    public PackageSymbol getPackage(final ModuleSymbol moduleElement,
                                    final Name fullName) {
        final var map = packages.get(nameResolver.resolveName(fullName));

        if (map == null) {
            return null;
        } else {
            return map.get(Objects.requireNonNullElse(moduleElement, NO_MODULE));
        }
    }

    private void doEnterPackage(final ModuleSymbol moduleElement,
                                final PackageSymbol packageSymbol) {
        final var qualifiedName = nameResolver.resolveName(packageSymbol.getQualifiedName());
        final var module = Objects.requireNonNullElse(moduleElement, NO_MODULE);
        packages.computeIfAbsent(qualifiedName, (k) -> new HashMap<>()).put(module, packageSymbol);
        packageSymbol.module = module;
    }

    public ClassSymbol enterClass(final ModuleSymbol moduleElement,
                                  final Name fullName) {
        var classSymbol = getClass(moduleElement, fullName);

        if (classSymbol != null) {
            return classSymbol;
        }

        classSymbol = new ClassSymbol(
                ElementKind.CLASS,
                fullName.shortName(),
                NestingKind.TOP_LEVEL,
                enterPackage(moduleElement, fullName.packagePart())
        );
        final var classType = new ClassType(classSymbol, true);
        classSymbol.setType(classType);

        doEnterClass(moduleElement, classSymbol);
        return classSymbol;
    }

    public ClassSymbol getClass(final ModuleSymbol moduleElement,
                                final Name fullName) {
        final var map = classes.get(nameResolver.resolveName(fullName));

        if (map == null) {
            return null;
        } else {
            final var classSymbol = map.get(Objects.requireNonNullElse(moduleElement, NO_MODULE));

            if (classSymbol != null) {
                return classSymbol;
            } else {
                return map.get(ModuleSymbol.UNNAMED);
            }
        }
    }

    public void doEnterClass(final ModuleSymbol moduleElement,
                             final ClassSymbol classSymbol) {
        final var qualifiedName = nameResolver.resolveName(classSymbol.getQualifiedName());
        final var module = Objects.requireNonNullElse(moduleElement, NO_MODULE);
        classes.computeIfAbsent(qualifiedName, (k) -> new HashMap<>()).put(module, classSymbol);

        final var enclosingElement = (AbstractSymbol) classSymbol.getEnclosingElement();

        if (enclosingElement != null) {
            enclosingElement.addEnclosedElement(classSymbol);
        }
    }

    public void addClass(final ModuleSymbol moduleElement,
                         final ClassSymbol classSymbol,
                         final Name fullName) {
        final var qualifiedName = nameResolver.resolveName(fullName);
        final var module = Objects.requireNonNullElse(moduleElement, NO_MODULE);
        classes.computeIfAbsent(qualifiedName, (k) -> new HashMap<>()).put(module, classSymbol);
    }
}
