package io.github.potjerodekool.codegen.model.util.type;

import io.github.potjerodekool.codegen.model.element.Element;
import io.github.potjerodekool.codegen.model.element.TypeElement;
import io.github.potjerodekool.codegen.model.type.*;
import io.github.potjerodekool.codegen.model.type.immutable.WildcardType;

import java.util.List;

public interface Types {

    Element asElement(TypeMirror t);

    boolean isSameType(TypeMirror t1, TypeMirror t2);

    boolean isSubtype(TypeMirror t1, TypeMirror t2);

    boolean isAssignable(TypeMirror t1, TypeMirror t2);

    boolean contains(TypeMirror t1, TypeMirror t2);

    boolean isSubsignature(ExecutableType m1, ExecutableType m2);

    List<? extends TypeMirror> directSupertypes(TypeMirror t);

    TypeMirror erasure(TypeMirror t);

    TypeElement boxedClass(PrimitiveType p);

    PrimitiveType unboxedType(TypeMirror t);

    TypeMirror capture(TypeMirror t);

    PrimitiveType getPrimitiveType(TypeKind kind);

    NullType getNullType();

    NoType getNoType(TypeKind kind);

    ArrayType getArrayType(TypeMirror componentType);

    WildcardType getWildcardType(TypeMirror extendsBound,
                                 TypeMirror superBound);

    DeclaredType getDeclaredType(TypeElement typeElem, TypeMirror... typeArgs);

    DeclaredType getDeclaredType(DeclaredType containing,
                                 TypeElement typeElem, TypeMirror... typeArgs);

    TypeMirror asMemberOf(DeclaredType containing, Element element);
}
