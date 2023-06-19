package io.github.potjerodekool.codegen.model.element;

public interface Name extends CharSequence {

    Name EMPTY = new NameImpl("");

    static Name of(final CharSequence value) {
        if (value.isEmpty()) {
            return Name.EMPTY;
        } else {
            return new NameImpl(value);
        }
    }

    static Name of(final Name parentName,
                          final Name childName) {
        if (parentName == Name.EMPTY) {
            return childName;
        } else {
            return of(parentName.toString() + "." + childName.toString());
        }
    }

    static Name getQualifiedNameOf(final Element element) {
        if (element instanceof QualifiedNameable qn) {
            return qn.getQualifiedName();
        } else {
            return element.getSimpleName();
        }
    }


    boolean equals(Object obj);

    int hashCode();

    boolean contentEquals(CharSequence cs);
}
