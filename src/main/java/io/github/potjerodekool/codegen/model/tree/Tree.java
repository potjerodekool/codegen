package io.github.potjerodekool.codegen.model.tree;

import io.github.potjerodekool.codegen.model.type.TypeMirror;

//TODO Replace with AbstractTree
public interface Tree {

    TypeMirror getType();

    void setType(TypeMirror type);

    <R,P> R accept(final TreeVisitor<R,P> visitor, P param);
}
