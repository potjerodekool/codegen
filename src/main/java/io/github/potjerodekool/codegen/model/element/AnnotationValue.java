package io.github.potjerodekool.codegen.model.element;

public interface AnnotationValue {


    Object getValue();

    String toString();

    <R, P> R accept(AnnotationValueVisitor<R, P> v, P p);
}
