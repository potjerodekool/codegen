package io.github.potjerodekool.codegen.model.element;

import io.github.potjerodekool.codegen.model.type.TypeMirror;

import java.util.List;

public interface ExecutableElement extends Element, Parameterizable {

    @Override
    TypeMirror asType();

    List<? extends TypeParameterElement> getTypeParameters();

    TypeMirror getReturnType();

    List<? extends VariableElement> getParameters();

    TypeMirror getReceiverType();

    boolean isVarArgs();

    boolean isDefault();

    List<? extends TypeMirror> getThrownTypes();

    AnnotationValue getDefaultValue();

    @Override
    Name getSimpleName();
}
