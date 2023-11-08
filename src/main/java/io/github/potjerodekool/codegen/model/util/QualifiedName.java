package io.github.potjerodekool.codegen.model.util;

import io.github.potjerodekool.codegen.model.element.Name;

public record QualifiedName(Name packageName,
                            Name simpleName) {

    private QualifiedName(final String packageName,
                          final String simpleName) {
        this(Name.of(packageName), Name.of(simpleName));
    }

    public static QualifiedName from(final Name className) {
        return from(className.toString());
    }

    public static QualifiedName from(final String className) {
        final var separatorIndex = className.lastIndexOf('.');
        if (separatorIndex < 0) {
            return new QualifiedName(Name.EMPTY, Name.of(className));
        } else {
            return new QualifiedName(className.substring(0, separatorIndex), className.substring(separatorIndex + 1));
        }
    }

    public String asString() {
        return toString();
    }

    @Override
    public String toString() {
        if (packageName.contentEquals("")) {
            return simpleName.toString();
        } else {
            return packageName + "." + simpleName;
        }
    }
}
