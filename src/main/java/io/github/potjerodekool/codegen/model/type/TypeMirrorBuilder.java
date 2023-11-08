package io.github.potjerodekool.codegen.model.type;

public abstract class TypeMirrorBuilder<TM extends TypeMirror> {

    public abstract TM build();
}
