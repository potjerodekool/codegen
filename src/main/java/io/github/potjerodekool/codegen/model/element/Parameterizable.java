package io.github.potjerodekool.codegen.model.element;

import java.util.List;

public interface Parameterizable extends Element {

    List<? extends TypeParameterElement> getTypeParameters();
}
