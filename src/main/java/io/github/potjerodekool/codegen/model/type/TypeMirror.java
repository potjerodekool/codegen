package io.github.potjerodekool.codegen.model.type;

import io.github.potjerodekool.codegen.model.AnnotatedConstruct;
import io.github.potjerodekool.codegen.model.element.AnnotationMirror;
import io.github.potjerodekool.codegen.model.type.immutable.WildcardType;

import java.lang.annotation.Annotation;
import java.util.List;
public interface TypeMirror extends AnnotatedConstruct {

    TypeKind getKind();

    boolean equals(Object obj);

    int hashCode();

    String toString();

    @Override
    List<? extends AnnotationMirror> getAnnotationMirrors();

    @Override
    <A extends Annotation> A getAnnotation(Class<A> annotationType);

    @Override
    <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationType);

    <R, P> R accept(TypeVisitor<R, P> v, P p);

    <R, P> R accept(TypeMirror.Visitor<R, P> v, P p);

    default boolean isPrimitiveType() {
        return false;
    }

    default boolean isArrayType() {
        return false;
    }

    default boolean isDeclaredType() {
        return false;
    }

    default boolean isWildCardType() { return false; }

    default boolean isVoidType() { return false; }

    default boolean isVarType() {
        return false;
    }

    default boolean isNullable() {
        return false;
    }

    TypeMirror asNullableType();

    TypeMirror asNonNullableType();

    default TypeMirrorBuilder<? extends TypeMirror> builder() {
        throw new UnsupportedOperationException();
    }

    interface Visitor<R,P> {

        R visitType(TypeMirror type, P p);

        R visitNoType(NoType noType, P p);

        R visitPrimitive(PrimitiveType primitiveType, P p);

        R visitArray(ArrayType arrayType, P p);

        R visitDeclared(DeclaredType declaredType, P p);

        R visitError(ErrorTypeImpl errorType, P p);

        R visitNull(NullType nullType, P p);

        R visitWildcard(WildcardType wildcardType, P param);

        R visitExecutable(ExecutableType executableType, P param);

        R visitTypeVariable(TypeVariable typeVariable, P p);

        R visitVarType(VarType varType, P p);
    }
}
