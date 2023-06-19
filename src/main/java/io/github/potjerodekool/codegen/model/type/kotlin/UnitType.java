package io.github.potjerodekool.codegen.model.type.kotlin;

import io.github.potjerodekool.codegen.model.element.AnnotationMirror;
import io.github.potjerodekool.codegen.model.element.Element;
import io.github.potjerodekool.codegen.model.type.NoType;
import io.github.potjerodekool.codegen.model.type.TypeKind;
import io.github.potjerodekool.codegen.model.type.TypeMirror;
import io.github.potjerodekool.codegen.model.type.TypeVisitor;

import java.lang.annotation.Annotation;
import java.util.List;

public class UnitType implements NoType, TypeMirror {

    public static final UnitType INSTANCE = new UnitType();

    private UnitType() {}

    @Override
    public TypeKind getKind() {
        return TypeKind.OTHER;
    }

    @Override
    public List<? extends AnnotationMirror> getAnnotationMirrors() {
        return null;
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
    public <R, P> R accept(final TypeVisitor<R, P> visitor,
                           final P param) {
        return visitor.visitNoType(this, param);
    }

    @Override
    public <R, P> R accept(final Visitor<R, P> v,
                           final P param) {
        return v.visitNoType(this, param);
    }

    @Override
    public TypeMirror asNullableType() {
        return null;
    }

    @Override
    public TypeMirror asNonNullableType() {
        return null;
    }

}
