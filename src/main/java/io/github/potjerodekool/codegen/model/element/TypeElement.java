package io.github.potjerodekool.codegen.model.element;

import io.github.potjerodekool.codegen.model.type.TypeMirror;

import java.util.List;

public interface TypeElement extends Element, Parameterizable, QualifiedNameable {

    @Override
    TypeMirror asType();

    @Override
    List<? extends Element> getEnclosedElements();

    NestingKind getNestingKind();

    Name getQualifiedName();

    @Override
    Name getSimpleName();

    TypeMirror getSuperclass();

    List<? extends TypeMirror> getInterfaces();

    List<? extends TypeParameterElement> getTypeParameters();

    default List<? extends RecordComponentElement> getRecordComponents() {
        return List.of();
    }

    default List<? extends TypeMirror> getPermittedSubclasses() {
        return List.of();
    }

    @Override
    Element getEnclosingElement();
}
