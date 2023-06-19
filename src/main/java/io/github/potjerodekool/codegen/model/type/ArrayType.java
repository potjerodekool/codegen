package io.github.potjerodekool.codegen.model.type;

public interface ArrayType extends TypeMirror {

    @Override
    default boolean isArrayType() {
        return true;
    }

    @Override
    default TypeKind getKind() {
        return TypeKind.ARRAY;
    }

    TypeMirror getComponentType();
}
