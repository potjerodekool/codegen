package io.github.potjerodekool.codegen;

import javax.tools.Diagnostic;

public interface DiagnosticListener<S> {

    void report(Diagnostic<? extends S> diagnostic);
}
