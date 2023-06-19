package io.github.potjerodekool.codegen.model.element;

import io.github.potjerodekool.codegen.model.type.TypeMirror;

public interface VariableElement extends Element {

    @Override
    TypeMirror asType();

    Object getConstantValue();

    @Override
    Name getSimpleName();

    @Override
    Element getEnclosingElement();
}
