package io.github.potjerodekool.codegen.model.type;


import io.github.potjerodekool.codegen.model.element.Element;

import java.util.List;

public interface DeclaredType extends ReferenceType {

    Element asElement();

    TypeMirror getEnclosingType();

    List<? extends TypeMirror> getTypeArguments();

    @Override
    DeclaredType asNullableType();

    @Override
    DeclaredType asNonNullableType();

}
