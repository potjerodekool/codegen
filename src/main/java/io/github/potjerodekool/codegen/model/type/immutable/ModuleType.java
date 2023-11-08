package io.github.potjerodekool.codegen.model.type.immutable;

import io.github.potjerodekool.codegen.model.element.AnnotationMirror;
import io.github.potjerodekool.codegen.model.type.NoType;
import io.github.potjerodekool.codegen.model.type.TypeKind;
import io.github.potjerodekool.codegen.model.type.TypeMirror;
import io.github.potjerodekool.codegen.model.type.TypeVisitor;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class ModuleType implements NoType {

    private final List<AnnotationMirror> annotationMirrors = new ArrayList<>();

    @Override
    public TypeKind getKind() {
        return TypeKind.MODULE;
    }

    @Override
    public List<? extends AnnotationMirror> getAnnotationMirrors() {
        return annotationMirrors;
    }

    @Override
    public <A extends Annotation> A getAnnotation(final Class<A> annotationType) {
        return null;
    }

    @Override
    public <A extends Annotation> A[] getAnnotationsByType(final Class<A> annotationType) {
        return null;
    }

    @Override
    public <R, P> R accept(final TypeVisitor<R, P> v, final P p) {
        return v.visitNoType(this, p);
    }

    @Override
    public <R, P> R accept(final Visitor<R, P> v, final P p) {
        return v.visitNoType(this, p);
    }

    @Override
    public TypeMirror asNullableType() {
        return this;
    }

    @Override
    public TypeMirror asNonNullableType() {
        return this;
    }
}
