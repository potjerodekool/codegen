package io.github.potjerodekool.codegen.model.util;

import io.github.potjerodekool.codegen.model.element.*;
import io.github.potjerodekool.codegen.model.element.java.ElementFilter;
import io.github.potjerodekool.codegen.model.symbol.ClassSymbol;
import io.github.potjerodekool.codegen.model.symbol.PackageSymbol;
import io.github.potjerodekool.codegen.model.type.DeclaredType;
import io.github.potjerodekool.codegen.model.type.TypeMirror;

import java.io.Writer;
import java.util.*;

public abstract class AbstractElements implements Elements {

    private final SymbolTable symbolTable;

    public AbstractElements(final SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    protected SymbolTable getSymbolTable() {
        return symbolTable;
    }

    @Override
    public PackageElement getPackageElement(final CharSequence name) {
        return symbolTable.getPackage(null, Name.of(name));
    }

    @Override
    public TypeElement getTypeElement(final ModuleElement module,
                                      final CharSequence name) {
        TypeElement typeElement = doGetTypeElement(name);

        if (typeElement != null) {
            return typeElement;
        }

        final var fullName = name.toString();

        var fromIndex = fullName.length() - 1;
        var index = fullName.lastIndexOf('.', fromIndex);

        if (index < 0) {
            return null;
        }

        fromIndex = index;
        String parentName;

        do {
            index = fullName.lastIndexOf('.', fromIndex - 1);
            parentName = fullName.substring(0, fromIndex);
            typeElement = doGetTypeElement(parentName);

            fromIndex = index;
        } while (typeElement == null && fromIndex > 0);

        if (typeElement == null) {
            return null;
        }

        final var childString = fullName.substring(fromIndex + 1);
        final var childNames = childString.split("\\.");

        for (int childIndex = 1; childIndex < childNames.length; childIndex++) {
            final var childName = childNames[childIndex];
            final var types = ElementFilter.typesIn(typeElement.getEnclosedElements());
            final var childOptional = types.stream()
                    .filter(it -> it.getSimpleName().contentEquals(childName))
                    .findFirst();

            if (childOptional.isEmpty()) {
                return null;
            } else {
                typeElement = childOptional.get();
            }
        }

        return typeElement;
    }

    private TypeElement doGetTypeElement(final CharSequence name) {
        var typeElement = findTypeElement(name);

        if (typeElement == null) {
            typeElement = doLoadTypeElement(name);
        }
        return typeElement;
    }

    protected TypeElement findTypeElement(final CharSequence name) {
        return symbolTable.getClass(null, Name.of(name));
    }

    protected TypeElement doLoadTypeElement(final CharSequence name) {
        return null;
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
    public Name getBinaryName(final TypeElement typeElement) {
        final var binaryNameBuilder = new StringBuilder();
        resolveBinaryName(typeElement, binaryNameBuilder);
        return Name.of(binaryNameBuilder.toString());
    }

    private void resolveBinaryName(final Element element,
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

    protected String toInternalName(final CharSequence name) {
        return name.toString().replace('.', '/')
                .replace('$', '.');
    }
}
