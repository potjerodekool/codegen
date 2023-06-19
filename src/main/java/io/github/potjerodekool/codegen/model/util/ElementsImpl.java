package io.github.potjerodekool.codegen.model.util;

import io.github.potjerodekool.codegen.loader.TypeElementLoader;
import io.github.potjerodekool.codegen.model.element.*;
import io.github.potjerodekool.codegen.model.type.DeclaredType;
import io.github.potjerodekool.codegen.model.type.TypeMirror;

import java.io.Writer;
import java.util.*;

public class ElementsImpl implements Elements {

    private final TypeElementLoader typeElementLoader;

    private final SymbolTable symbolTable;

    public ElementsImpl(final TypeElementLoader typeElementLoader,
                        final SymbolTable symbolTable) {
        this.typeElementLoader = typeElementLoader;
        this.symbolTable = symbolTable;
    }

    @Override
    public PackageElement getPackageElement(final CharSequence name) {
        return symbolTable.findPackage(Name.of(name));
    }

    @Override
    public TypeElement getTypeElement(final CharSequence name) {
        var typeElement = symbolTable.findClass(Name.of(name));

        if (typeElement == null) {
            typeElement = typeElementLoader.loadTypeElement(name.toString());
        }
        return typeElement;
    }

    @Override
    public Map<? extends ExecutableElement, ? extends AnnotationValue> getElementValuesWithDefaults(final AnnotationMirror a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getDocComment(final Element e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDeprecated(final Element e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Name getBinaryName(final TypeElement type) {
        return symbolTable.getBinaryName(type);
    }

    @Override
    public PackageElement getPackageOf(final Element e) {
        if (e instanceof PackageElement pe) {
            return pe;
        } else {
            final var enclosingElement = e.getEnclosingElement();
            return enclosingElement != null
                    ? getPackageOf(enclosingElement)
                    : null;
        }
    }

    @Override
    public List<? extends Element> getAllMembers(final TypeElement type) {
        final var allMembers = new ArrayList<Element>();
        findAllMembers(type, allMembers, new HashSet<>());
        return allMembers;
    }

    private void findAllMembers(final TypeElement type,
                                final List<Element> allMembers,
                                final Set<TypeElement> processed) {
        if (processed.contains(type)) {
            return;
        }

        processed.add(type);

        allMembers.addAll(type.getEnclosedElements());

        if (type.getSuperclass() != null) {
            findAllMembers((TypeElement) ((DeclaredType)type.getSuperclass()).asElement(), allMembers, processed);
        }

        for (final TypeMirror anInterface : type.getInterfaces()) {
            findAllMembers((TypeElement) ((DeclaredType) anInterface).asElement(), allMembers, processed);
        }
    }

    @Override
    public List<? extends AnnotationMirror> getAllAnnotationMirrors(final Element e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hides(final Element hider, final Element hidden) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean overrides(final ExecutableElement overrider, final ExecutableElement overridden, final TypeElement type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getConstantExpression(final Object value) {
        if (value == null) {
            return "null";
        } else if (value instanceof String) {
            return "\"" + value + "\"";
        } else {
            return value.toString();
        }
    }

    @Override
    public void printElements(final Writer w, final Element... elements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Name getName(final CharSequence cs) {
        return Name.of(cs);
    }

    @Override
    public boolean isFunctionalInterface(final TypeElement type) {
        if (type.getKind() != ElementKind.INTERFACE) {
            return true;
        }

        return type.getEnclosedElements().stream()
                .filter(enclosedElement -> enclosedElement.getKind() == ElementKind.METHOD)
                .filter(enclosedElement -> enclosedElement.getModifiers().contains(Modifier.ABSTRACT))
                .map(enclosedElement -> (ExecutableElement) enclosedElement)
                .filter(method -> !method.isDefault())
                .count() == 1;
    }

    public Name getQualifiedName(final Element element) {
        if (element instanceof QualifiedNameable qn) {
            return qn.getQualifiedName();
        } else {
            return element.getSimpleName();
        }
    }
}
