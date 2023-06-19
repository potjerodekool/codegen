package io.github.potjerodekool.codegen.model.element;

import io.github.potjerodekool.codegen.model.type.TypeMirror;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;


public interface Element {

    TypeMirror asType();

    ElementKind getKind();

    Set<Modifier> getModifiers();

    Name getSimpleName();

    Element getEnclosingElement();

    List<? extends Element> getEnclosedElements();

    List<? extends AnnotationMirror> getAnnotationMirrors();

    <A extends Annotation> A getAnnotation(Class<A> annotationType);

    <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationType);

    <R, P> R accept(ElementVisitor<R, P> v, P p);
}
