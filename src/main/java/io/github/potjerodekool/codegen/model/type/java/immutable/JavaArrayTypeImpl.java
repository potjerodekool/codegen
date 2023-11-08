package io.github.potjerodekool.codegen.model.type.java.immutable;

import io.github.potjerodekool.codegen.model.element.AnnotationMirror;
import io.github.potjerodekool.codegen.model.type.ArrayType;
import io.github.potjerodekool.codegen.model.type.TypeMirror;
import io.github.potjerodekool.codegen.model.type.TypeVisitor;
import io.github.potjerodekool.codegen.model.type.java.JavaArrayType;

import java.lang.annotation.Annotation;
import java.util.List;

public class JavaArrayTypeImpl implements JavaArrayType, ArrayType {

    private final TypeMirror componentType;
    private final boolean isNullable;

    public JavaArrayTypeImpl(final TypeMirror componentType,
                             final boolean isNullable) {
        this.componentType = componentType;
        this.isNullable = isNullable;
    }

    @Override
    public List<AnnotationMirror> getAnnotationMirrors() {
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
        return v.visitArray(this, p);
    }

    @Override
    public <R, P> R accept(final Visitor<R, P> visitor, final P p) {
        return visitor.visitArray(this, p);
    }

    @Override
    public TypeMirror getComponentType() {
        return componentType;
    }

    @Override
    public TypeMirror asNullableType() {
        return isNullable ? this : new JavaArrayTypeImpl(componentType, true);
    }

    @Override
    public TypeMirror asNonNullableType() {
        return !isNullable ? this : new JavaArrayTypeImpl(componentType, false);
    }

    @Override
    public boolean isNullable() {
        return isNullable;
    }

    @Override
    public String toString() {
        return "[" + componentType;
    }
}
