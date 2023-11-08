package io.github.potjerodekool.codegen.model.type.immutable;

import io.github.potjerodekool.codegen.model.element.AnnotationMirror;
import io.github.potjerodekool.codegen.model.type.TypeKind;
import io.github.potjerodekool.codegen.model.type.TypeMirror;
import io.github.potjerodekool.codegen.model.type.TypeVisitor;
import io.github.potjerodekool.codegen.model.type.VarType;

import java.lang.annotation.Annotation;
import java.util.List;

public class VarTypeImpl implements VarType {

    private TypeMirror interferedType;

    @Override
    public TypeMirror getInterferedType() {
        return interferedType;
    }

    public void setInterferedType(final TypeMirror interferedType) {
        this.interferedType = interferedType;
    }

    @Override
    public TypeKind getKind() {
        if (interferedType == null) {
            return TypeKind.OTHER;
        }
        return interferedType.getKind();
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
        return v.visitVarType(this, p);
    }


    @Override
    public <R, P> R accept(final TypeMirror.Visitor<R, P> visitor, final P p) {
        return visitor.visitVarType(this, p);
    }

    @Override
    public boolean isVarType() {
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
}
