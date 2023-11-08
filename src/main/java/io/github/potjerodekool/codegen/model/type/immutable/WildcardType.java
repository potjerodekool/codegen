package io.github.potjerodekool.codegen.model.type.immutable;

import io.github.potjerodekool.codegen.model.element.AnnotationMirror;
import io.github.potjerodekool.codegen.model.type.TypeKind;
import io.github.potjerodekool.codegen.model.type.TypeMirror;
import io.github.potjerodekool.codegen.model.type.TypeVisitor;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.annotation.Annotation;
import java.util.List;

public class WildcardType implements TypeMirror  {

    private final @Nullable TypeMirror extendsBound;
    private final @Nullable TypeMirror superBound;
    private final boolean isNullable;

    public static WildcardType create() {
        return new WildcardType(null, null, false);
    }

    public static WildcardType create(final boolean isNullable) {
        return new WildcardType(null, null, isNullable);
    }

    public static WildcardType withExtendsBound(final TypeMirror extendsBound) {
        return new WildcardType(extendsBound, null, false);
    }

    public static WildcardType withSuperBound(final TypeMirror superBound) {
        return new WildcardType(null, superBound, false);
    }

    private WildcardType(final @Nullable TypeMirror extendsBound,
                         final @Nullable TypeMirror superBound,
                         final boolean isNullable) {
        this.extendsBound = extendsBound;
        this.superBound = superBound;
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

    public TypeMirror getExtendsBound() {
        return extendsBound;
    }

    public TypeMirror getSuperBound() {
        return superBound;
    }

    @Override
    public <R,P> R accept(final TypeVisitor<R,P> visitor,
                          final P param) {
        return visitor.visitWildcard(this, param);
    }

    @Override
    public <R, P> R accept(final Visitor<R, P> visitor, final P p) {
        return visitor.visitWildcard(this, p);
    }

    @Override
    public TypeKind getKind() {
        return TypeKind.WILDCARD;
    }

    @Override
    public boolean isWildCardType() {
        return true;
    }

    @Override
    public TypeMirror asNullableType() {
        return isNullable ? this : new WildcardType(
                extendsBound,
                superBound,
                true
        );
    }

    @Override
    public TypeMirror asNonNullableType() {
        return !isNullable ? this : new WildcardType(
                extendsBound,
                superBound,
                false
        );
    }

    @Override
    public boolean isNullable() {
        return isNullable;
    }

    @Override
    public String toString() {
        if (extendsBound != null) {
            return "? extends " + extendsBound;
        } else if (superBound != null) {
            return "? super " + superBound;
        } else {
            return "?";
        }
    }
}
