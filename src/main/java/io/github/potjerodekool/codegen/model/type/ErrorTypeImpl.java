package io.github.potjerodekool.codegen.model.type;

import io.github.potjerodekool.codegen.model.element.AnnotationMirror;
import io.github.potjerodekool.codegen.model.symbol.ClassSymbol;

import java.util.List;

public class ErrorTypeImpl extends ClassType implements ErrorType {

    public ErrorTypeImpl(final ClassSymbol typeElement) {
        this(typeElement,  List.of(), List.of(), false);
    }

    public ErrorTypeImpl(final ClassSymbol typeElement,
                         final boolean isNullable) {
        this(typeElement, List.of(), List.of(), isNullable);
    }

    public ErrorTypeImpl(final ClassSymbol typeElement,
                         final List<? extends AnnotationMirror> annotations,
                         final List<? extends TypeMirror> typeArguments,
                         final boolean isNullable) {
        super(typeElement, annotations, typeArguments, isNullable);
    }

    @Override
    public List<? extends TypeMirror> getTypeArguments() {
        return List.of();
    }

    @Override
    public List<AnnotationMirror> getAnnotationMirrors() {
        return List.of();
    }

    @Override
    public ErrorTypeImpl asNullableType() {
        if (isNullable()) {
            return this;
        }
        return new ErrorTypeImpl(asElement(), true);
    }

    @Override
    public ErrorTypeImpl asNonNullableType() {
        if (!isNullable()) {
            return this;
        }
        return new ErrorTypeImpl(asElement(), false);
    }

    @Override
    public <R, P> R accept(final TypeVisitor<R, P> visitor, final P param) {
        return visitor.visitError(this, param);
    }

    @Override
    public <R, P> R accept(final Visitor<R, P> visitor, final P p) {
        return visitor.visitError(this, p);
    }

    @Override
    public TypeKind getKind() {
        return TypeKind.ERROR;
    }


}
