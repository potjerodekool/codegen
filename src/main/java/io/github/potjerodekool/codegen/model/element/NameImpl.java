package io.github.potjerodekool.codegen.model.element;

public class NameImpl implements Name {

    private final CharSequence value;

    NameImpl(final CharSequence value) {
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
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        return obj == this
                || (obj instanceof Name otherName && value.equals(otherName.toString()));
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
