package io.github.potjerodekool.codegen.model.type.immutable;

import io.github.potjerodekool.codegen.model.element.AnnotationMirror;
import io.github.potjerodekool.codegen.model.type.PrimitiveType;
import io.github.potjerodekool.codegen.model.type.TypeKind;
import io.github.potjerodekool.codegen.model.type.TypeMirror;
import io.github.potjerodekool.codegen.model.type.TypeVisitor;

import java.lang.annotation.Annotation;
import java.util.List;

public class PrimitiveTypeImpl implements TypeMirror, PrimitiveType {

    public static final PrimitiveTypeImpl BOOLEAN = new PrimitiveTypeImpl(TypeKind.BOOLEAN);
    public static final PrimitiveTypeImpl BYTE = new PrimitiveTypeImpl(TypeKind.BYTE);
    public static final PrimitiveTypeImpl SHORT = new PrimitiveTypeImpl(TypeKind.SHORT);
    public static final PrimitiveTypeImpl INT = new PrimitiveTypeImpl(TypeKind.INT);
    public static final PrimitiveTypeImpl LONG = new PrimitiveTypeImpl(TypeKind.LONG);
    public static final PrimitiveTypeImpl CHAR = new PrimitiveTypeImpl(TypeKind.CHAR);
    public static final PrimitiveTypeImpl FLOAT = new PrimitiveTypeImpl(TypeKind.FLOAT);
    public static final PrimitiveTypeImpl DOUBLE = new PrimitiveTypeImpl(TypeKind.DOUBLE);

    private final TypeKind kind;

    public PrimitiveTypeImpl(final TypeKind kind) {
        this.kind = kind;
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
        return v.visitPrimitive(this, p);
    }

    @Override
    public <R, P> R accept(final Visitor<R, P> visitor, final P p) {
        return visitor.visitPrimitive(this, p);
    }

    @Override
    public TypeKind getKind() {
        return kind;
    }

    @Override
    public boolean isPrimitiveType() {
        return true;
    }


    @Override
    public TypeMirror asNullableType() {
        return this;
    }

    @Override
    public TypeMirror asNonNullableType() {
        return this;
    }

    @Override
    public String toString() {
        return kind.name().toLowerCase();
    }
}
