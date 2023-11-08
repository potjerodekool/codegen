package io.github.potjerodekool.codegen.model.util.type;

public class InvalidTypeArgumentsCountException extends IllegalArgumentException {

    public InvalidTypeArgumentsCountException(final int expectedCount,
                                              final int typeParameterCount) {
        super(String.format("Expected %s type arguments but got %s", expectedCount, typeParameterCount));
    }
}
