package io.github.potjerodekool.codegen.model;

import io.github.potjerodekool.codegen.model.element.AnnotationMirror;

import java.lang.annotation.Annotation;
import java.util.List;

public interface AnnotatedConstruct {

    List<? extends AnnotationMirror> getAnnotationMirrors();

    <A extends Annotation> A getAnnotation(Class<A> annotationType);

    <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationType);
}
