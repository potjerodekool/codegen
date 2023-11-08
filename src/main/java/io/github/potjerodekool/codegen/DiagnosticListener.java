package io.github.potjerodekool.codegen;

public interface DiagnosticListener<S> {

    void report(Diagnostic<? extends S> diagnostic);
}
