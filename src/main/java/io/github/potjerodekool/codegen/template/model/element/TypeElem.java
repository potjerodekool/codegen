package io.github.potjerodekool.codegen.template.model.element;

import io.github.potjerodekool.codegen.model.element.ElementKind;

public class TypeElem extends AbstractElem<TypeElem> {

    public MethodElem createConstructor() {
        final var constructor = new MethodElem()
                .withSimpleName(getSimpleName())
                .withKind(ElementKind.CONSTRUCTOR);

        withEnclosedElement(constructor);
        return constructor;
    }

    @Override
    public <P, R> R accept(final ElementVisitor<P, R> visitor, final P p) {
        return visitor.visitTypeElement(this, p);
    }
}
