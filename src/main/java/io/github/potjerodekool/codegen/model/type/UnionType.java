package io.github.potjerodekool.codegen.model.type;

import java.util.List;

public interface UnionType extends TypeMirror {

    List<? extends TypeMirror> getAlternatives();
}
