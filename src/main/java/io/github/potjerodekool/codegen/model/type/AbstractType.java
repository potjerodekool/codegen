package io.github.potjerodekool.codegen.model.type;

import io.github.potjerodekool.codegen.model.element.AnnotationMirror;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractType implements TypeMirror {

    private final List<AnnotationMirror> annotations = new ArrayList<>();
    private final List<TypeMirror> typeArguments;

    public AbstractType() {
        this.typeArguments = new ArrayList<>();
    }

    public AbstractType(final List<? extends AnnotationMirror> annotations,
                        final List<TypeMirror> typeArguments) {
        this.annotations.addAll(annotations);
        this.typeArguments = typeArguments;
    }

    @Override
    public List<? extends AnnotationMirror> getAnnotationMirrors() {
        return Collections.unmodifiableList(annotations);
    }

    public <A extends Annotation> A getAnnotation(final Class<A> annotationType) {
        return null;
    }

    public <A extends Annotation> A[] getAnnotationsByType(final Class<A> annotationType) {
        return null;
    }

    public void addAnnotation(final AnnotationMirror annotation) {
        this.annotations.add(annotation);
    }

    public List<? extends TypeMirror> getTypeArguments() {
        return Collections.unmodifiableList(typeArguments);
    }

    public void addTypeArgument(final TypeMirror typeArgument) {
        this.typeArguments.add(typeArgument);
    }

    public abstract <R, P> R accept(Visitor<R, P> visitor, P p);

    @Override
    public TypeMirror asNullableType() {
        return this;
    }

    @Override
    public TypeMirror asNonNullableType() {
        return this;
    }
}
