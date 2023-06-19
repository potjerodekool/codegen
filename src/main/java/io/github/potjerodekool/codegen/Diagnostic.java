package io.github.potjerodekool.codegen;

public interface Diagnostic<S> {

    Kind kind();

    String message();

    S source();

    enum Kind {
        ERROR,
        WARNING
    }

}
