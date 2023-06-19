package io.github.potjerodekool.codegen.model.type.kotlin;

import io.github.potjerodekool.codegen.model.element.AnnotationMirror;
import io.github.potjerodekool.codegen.model.type.ArrayType;
import io.github.potjerodekool.codegen.model.type.TypeMirror;
import io.github.potjerodekool.codegen.model.type.TypeVisitor;

import java.lang.annotation.Annotation;
import java.util.List;

public class KotlinArrayType implements ArrayType {

    private final TypeMirror componentType;
    private final boolean isNullable;

    public KotlinArrayType(final TypeMirror componentType,
                           final boolean isNullable) {
        this.componentType = componentType;
        this.isNullable = isNullable;
    }

    @Override
    public List<AnnotationMirror> getAnnotationMirrors() {
        return List.of();
    }

    @Override
    public TypeMirror getComponentType() {
        return componentType;
    }

    @Override
    public <R, P> R accept(final TypeVisitor<R, P> visitor,
                           final P param) {
        return visitor.visitArray(this, param);
    }

    @Override
    public <R, P> R accept(final Visitor<R, P> v,
                           final P param) {
        return v.visitArray(this, param);
    }

    @Override
    public TypeMirror asNullableType() {
        return isNullable ? this : new KotlinArrayType(componentType, true);
    }

    @Override
    public TypeMirror asNonNullableType() {
        return !isNullable ? this : new KotlinArrayType(componentType, false);
    }

    @Override
    public boolean isNullable() {
        return isNullable;
    }

    @Override
    public <A extends Annotation> A[] getAnnotationsByType(final Class<A> annotationType) {
        return null;
    }

    @Override
    public <A extends Annotation> A getAnnotation(final Class<A> annotationType) {
        return null;
    }
}
