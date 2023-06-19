package io.github.potjerodekool.codegen.model.type;

import io.github.potjerodekool.codegen.model.element.Element;

public interface TypeVariable extends ReferenceType {

    Element asElement();

    TypeMirror getUpperBound();

    TypeMirror getLowerBound();
}
