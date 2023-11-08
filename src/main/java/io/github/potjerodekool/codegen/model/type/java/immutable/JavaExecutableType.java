package io.github.potjerodekool.codegen.model.type.java.immutable;

import io.github.potjerodekool.codegen.model.element.AnnotationMirror;
import io.github.potjerodekool.codegen.model.type.*;

import java.lang.annotation.Annotation;
import java.util.List;

public class JavaExecutableType implements ExecutableType {

    public JavaExecutableType() {
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
    public <R,P> R accept(final TypeVisitor<R, P> visitor,
                          final P param) {
        return visitor.visitExecutable(this, param);
    }

    @Override
    public <R, P> R accept(final TypeMirror.Visitor<R, P> visitor,
                           final P param) {
        return visitor.visitExecutable(this, param);
    }

    @Override
    public TypeKind getKind() {
        return TypeKind.EXECUTABLE;
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
    public List<? extends TypeVariable> getTypeVariables() {
        return null;
    }

    @Override
    public TypeMirror getReturnType() {
        return null;
    }

    @Override
    public List<? extends TypeMirror> getParameterTypes() {
        return null;
    }

    @Override
    public TypeMirror getReceiverType() {
        return null;
    }

    @Override
    public List<? extends TypeMirror> getThrownTypes() {
        return null;
    }
}
