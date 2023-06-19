package io.github.potjerodekool.codegen.model.type;

import io.github.potjerodekool.codegen.model.element.AnnotationMirror;
import io.github.potjerodekool.codegen.model.symbol.PackageSymbol;

import java.lang.annotation.Annotation;
import java.util.List;

public class PackageType extends AbstractType implements NoType {

    private final PackageSymbol element;

    public PackageType(final PackageSymbol packageElement) {
        this.element = packageElement;
    }

    public PackageSymbol asElement() {
        return element;
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
        return v.visitNoType(this, p);
    }

    @Override
    public <R, P> R accept(final Visitor<R, P> visitor, final P p) {
        return visitor.visitNoType(this, p);
    }

    @Override
    public TypeKind getKind() {
        return TypeKind.PACKAGE;
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
