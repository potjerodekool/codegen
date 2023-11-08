package io.github.potjerodekool.codegen.model.tree;

import io.github.potjerodekool.codegen.model.type.TypeMirror;

public interface Tree {

    TypeMirror getType();

    void setType(TypeMirror type);

    <R,P> R accept(final TreeVisitor<R,P> visitor, P param);
}
