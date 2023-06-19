package io.github.potjerodekool.codegen.model.element;

import io.github.potjerodekool.codegen.model.type.TypeMirror;

import java.util.List;

public interface PackageElement extends Element, QualifiedNameable {

    @Override
    TypeMirror asType();

    Name getQualifiedName();

    @Override
    Name getSimpleName();

    @Override
    List<? extends Element> getEnclosedElements();

    boolean isUnnamed();

    @Override
    Element getEnclosingElement();
}
