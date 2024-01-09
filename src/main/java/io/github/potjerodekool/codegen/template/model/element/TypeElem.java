package io.github.potjerodekool.codegen.template.model.element;

import io.github.potjerodekool.codegen.model.element.ElementKind;
import io.github.potjerodekool.codegen.template.model.expression.ClassOrInterfaceTypeExpr;

import java.util.ArrayList;
import java.util.List;

public class TypeElem extends AbstractElem<TypeElem> {

    private List<ClassOrInterfaceTypeExpr> implementing = new ArrayList<>();

    public MethodElem createConstructor() {
        final var constructor = new MethodElem()
                .simpleName(getSimpleName())
                .kind(ElementKind.CONSTRUCTOR);

        enclosedElement(constructor);
        return constructor;
    }

    public List<ClassOrInterfaceTypeExpr> getImplementing() {
        return implementing;
    }

    public TypeElem implement(final ClassOrInterfaceTypeExpr typeExpr) {
        implementing.add(typeExpr);
        return this;
    }

    @Override
    public <P, R> R accept(final ElementVisitor<P, R> visitor, final P p) {
        return visitor.visitTypeElement(this, p);
    }
}
