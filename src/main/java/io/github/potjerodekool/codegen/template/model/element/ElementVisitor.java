package io.github.potjerodekool.codegen.template.model.element;

public interface ElementVisitor<P,R> {

    R visitExecutableElement(MethodElem methodElement, P p);

    R visitTypeElement(TypeElem typeElement, P p);

    R visitVariableElement(VariableElem variableElement, P p);
}
