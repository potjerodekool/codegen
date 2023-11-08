package io.github.potjerodekool.codegen.model.element;

public interface Name extends CharSequence {

    Name EMPTY = NameCache.INSTANCE.EMPTY;

    static Name of(final CharSequence value) {
        if (value instanceof Name name) {
            return name;
        }

        var name = NameCache.INSTANCE.findName(value);

        if (name == null) {
            name = new NameImpl(value.toString());
            NameCache.INSTANCE.add(name);
        }

        return name;
    }

    static Name of(final Name parentName,
                   final Name childName) {
        if (parentName == EMPTY) {
            return childName;
        } else {
            return of(parentName.getValue() + "." + childName.getValue());
        }
    }

    static Name getQualifiedNameOf(final Element element) {
        if (element instanceof QualifiedNameable qn) {
            return qn.getQualifiedName();
        } else {
            return element.getSimpleName();
        }
    }


    Name shortName();

    Name packagePart();

    boolean equals(Object obj);

    int hashCode();

    boolean contentEquals(CharSequence cs);

    Name append(CharSequence simpleName);

    CharSequence getValue();
}
