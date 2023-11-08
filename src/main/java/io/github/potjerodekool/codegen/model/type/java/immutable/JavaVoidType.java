package io.github.potjerodekool.codegen.model.type.java.immutable;

import io.github.potjerodekool.codegen.model.element.AnnotationMirror;
import io.github.potjerodekool.codegen.model.type.NoType;
import io.github.potjerodekool.codegen.model.type.TypeKind;
import io.github.potjerodekool.codegen.model.type.TypeMirror;
import io.github.potjerodekool.codegen.model.type.TypeVisitor;

import java.lang.annotation.Annotation;
import java.util.List;

public class JavaVoidType implements NoType {

    public static final JavaVoidType INSTANCE = new JavaVoidType();

    private JavaVoidType() {
    }

    @Override
    public TypeKind getKind() {
        return TypeKind.VOID;
    }

    @Override
    public List<? extends AnnotationMirror> getAnnotationMirrors() {
        return List.of();
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
    public <R, P> R accept(final Visitor<R, P> visitor, final P p) {
        return visitor.visitNoType(this, p);
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
