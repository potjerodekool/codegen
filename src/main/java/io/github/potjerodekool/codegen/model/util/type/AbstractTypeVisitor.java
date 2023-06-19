package io.github.potjerodekool.codegen.model.util.type;

import io.github.potjerodekool.codegen.model.type.TypeMirror;

abstract class AbstractTypeVisitor<R,S> implements TypeMirror.Visitor<R,S> {

    AbstractTypeVisitor() {
    }

    public final R visit(final TypeMirror type,
                         final S s) {
        return type.accept(this, s);
    }
}
