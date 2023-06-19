package io.github.potjerodekool.codegen.model.element;

public interface RecordComponentElement extends Element {

    @Override
    Element getEnclosingElement();

    @Override
    Name getSimpleName();

    ExecutableElement getAccessor();
}
