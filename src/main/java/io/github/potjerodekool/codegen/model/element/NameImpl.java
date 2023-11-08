package io.github.potjerodekool.codegen.model.element;

public class NameImpl implements Name {

    private final String value;

    NameImpl(final String value) {
        this.value = value;
    }

    @Override
    public boolean contentEquals(final CharSequence cs) {
        return value.toString().contentEquals(cs);
    }

    @Override
    public int length() {
        return value.length();
    }

    @Override
    public char charAt(final int index) {
        return value.charAt(index);
    }

    @Override
    public CharSequence subSequence(final int start, final int end) {
        return value.subSequence(start, end);
    }

    @Override
    public Name append(final CharSequence childName) {
        return new NameImpl(value + "." + childName);
    }

    public CharSequence getValue() {
        return value;
    }

    @Override
    public Name shortName() {
        final var start = value.lastIndexOf('.') + 1;

        if (start == 0) {
            return this;
        }

        final var shortNameValue = value.substring(start);
        return Name.of(shortNameValue);
    }

    @Override
    public Name packagePart() {
        final var end = value.lastIndexOf('.');

        if (end < 0) {
            return Name.EMPTY;
        } else {
            return Name.of(value.substring(0, end));
        }
    }


    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Name other)) {
            return false;
        }

        return value.equals(other.toString());
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
