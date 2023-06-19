package io.github.potjerodekool.codegen.model.util.type;

import io.github.potjerodekool.codegen.model.type.TypeMirror;
import io.github.potjerodekool.codegen.model.type.kotlin.KotlinArrayType;

public class KotlinTypes extends DelegatingTypes {

    public KotlinTypes(final JavaTypes types) {
        super(types);
    }

    @Override
    public io.github.potjerodekool.codegen.model.type.ArrayType getArrayType(final TypeMirror componentType) {
        return new KotlinArrayType(componentType, false);
    }

}
