package io.github.potjerodekool.codegen.model.type;

import io.github.potjerodekool.codegen.model.element.AnnotationMirror;
import io.github.potjerodekool.codegen.model.symbol.TypeVariableSymbol;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.annotation.Annotation;
import java.util.List;

public class TypeVariableImpl implements TypeVariable {

    private final TypeVariableSymbol typeVariableSymbol;
    private TypeMirror upperBound = null;
    private TypeMirror lowerBound = null;

    public TypeVariableImpl(final TypeVariableSymbol typeVariableSymbol) {
        this.typeVariableSymbol = typeVariableSymbol;
    }

    @Override
    public <R, P> R accept(final TypeVisitor<R, P> visitor, final P param) {
        return visitor.visitTypeVariable(this, param);
    }

    @Override
    public <R, P> R accept(final TypeMirror.Visitor<R, P> visitor, final P p) {
        return visitor.visitTypeVariable(this, p);
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
    public @Nullable TypeVariableSymbol asElement() {
        return typeVariableSymbol;
    }

    @Override
    public TypeMirror getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(final TypeMirror upperBound) {
        this.upperBound = upperBound;
    }

    @Override
    public TypeMirror getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(final TypeMirror lowerBound) {
        this.lowerBound = lowerBound;
    }

    @Override
    public TypeKind getKind() {
        return TypeKind.TYPEVAR;
    }

    @Override
    public String toString() {
        final var builder = new StringBuilder();
        builder.append(typeVariableSymbol.getSimpleName());

        if (upperBound != null) {
            builder.append(" extends ").append(upperBound);
        } else if (lowerBound != null) {
            builder.append(" super ").append(lowerBound);
        }

        return builder.toString();
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
}
