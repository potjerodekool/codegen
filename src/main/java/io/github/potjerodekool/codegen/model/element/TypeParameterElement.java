package io.github.potjerodekool.codegen.model.element;

import io.github.potjerodekool.codegen.model.type.TypeMirror;

import java.util.List;

public interface TypeParameterElement extends Element {

    @Override
    TypeMirror asType();

    Element getGenericElement();

    List<? extends TypeMirror> getBounds();

    @Override
    Element getEnclosingElement();
}
