package io.github.potjerodekool.codegen.model;

import io.github.potjerodekool.codegen.model.element.ModuleElement;

public class UnknownDirectiveException extends UnknownEntityException {

    private final transient ModuleElement.Directive directive;
    private final transient Object parameter;

    public UnknownDirectiveException(ModuleElement.Directive d, Object p) {
        super("Unknown directive: " + d);
        directive = d;
        parameter = p;
    }

    public ModuleElement.Directive getUnknownDirective() {
        return directive;
    }

    public Object getArgument() {
        return parameter;
    }
}
