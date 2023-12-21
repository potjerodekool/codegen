package io.github.potjerodekool.codegen.template.model.annotation;

public interface AnnotationVisitor<P, R> {

    R visitAnnotation(Annot annotation, P p);
}
