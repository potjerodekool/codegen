package io.github.potjerodekool.codegen;

public record DefaultDiagnostic<S>(Kind kind, String message, S source) implements Diagnostic<S> {
}
