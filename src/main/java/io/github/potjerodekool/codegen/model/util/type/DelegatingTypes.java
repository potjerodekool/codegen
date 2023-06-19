package io.github.potjerodekool.codegen.model.util.type;

import io.github.potjerodekool.codegen.model.element.Element;
import io.github.potjerodekool.codegen.model.element.TypeElement;
import io.github.potjerodekool.codegen.model.type.*;

import java.util.List;

public abstract class DelegatingTypes implements Types {

    private final Types types;

    public DelegatingTypes(final Types types) {
        this.types = types;
    }

    protected Types getTypes() {
        return types;
    }

    @Override
    public Element asElement(final TypeMirror t) {
        return types.asElement(t);
    }

    @Override
    public boolean isSameType(final TypeMirror t1, final TypeMirror t2) {
        return types.isSameType(t1, t2);
    }

    @Override
    public boolean isSubtype(final TypeMirror t1, final TypeMirror t2) {
        return types.isSubtype(t1, t2);
    }

    @Override
    public boolean isAssignable(final TypeMirror t1, final TypeMirror t2) {
        return types.isAssignable(t1, t2);
    }

    @Override
    public boolean contains(final TypeMirror t1, final TypeMirror t2) {
        return types.contains(t1, t2);
    }

    @Override
    public boolean isSubsignature(final ExecutableType m1, final ExecutableType m2) {
        return types.isSubsignature(m1, m2);
    }

    @Override
    public List<? extends TypeMirror> directSupertypes(final TypeMirror t) {
        return types.directSupertypes(t);
    }

    @Override
    public TypeMirror erasure(final TypeMirror t) {
        return types.erasure(t);
    }

    @Override
    public TypeElement boxedClass(final PrimitiveType p) {
        return types.boxedClass(p);
    }

    @Override
    public PrimitiveType unboxedType(final TypeMirror t) {
        return types.unboxedType(t);
    }

    @Override
    public TypeMirror capture(final TypeMirror t) {
        return types.capture(t);
    }

    @Override
    public PrimitiveType getPrimitiveType(final TypeKind kind) {
        return types.getPrimitiveType(kind);
    }

    @Override
    public NullType getNullType() {
        return types.getNullType();
    }

    @Override
    public NoType getNoType(final TypeKind kind) {
        return types.getNoType(kind);
    }

    @Override
    public ArrayType getArrayType(final TypeMirror componentType) {
        return types.getArrayType(componentType);
    }

    @Override
    public WildcardType getWildcardType(final TypeMirror extendsBound, final TypeMirror superBound) {
        return types.getWildcardType(extendsBound, superBound);
    }

    @Override
    public DeclaredType getDeclaredType(final TypeElement typeElem, final TypeMirror... typeArgs) {
        return types.getDeclaredType(typeElem, typeArgs);
    }

    @Override
    public DeclaredType getDeclaredType(final DeclaredType containing, final TypeElement typeElem, final TypeMirror... typeArgs) {
        return types.getDeclaredType(containing, typeElem, typeArgs);
    }

    @Override
    public TypeMirror asMemberOf(final DeclaredType containing, final Element element) {
        return types.asMemberOf(containing, element);
    }
}
