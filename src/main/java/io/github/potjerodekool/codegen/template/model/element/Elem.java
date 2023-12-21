package io.github.potjerodekool.codegen.template.model.element;

import io.github.potjerodekool.codegen.model.element.ElementKind;
import io.github.potjerodekool.codegen.model.element.Modifier;
import io.github.potjerodekool.codegen.template.model.annotation.Annot;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface Elem<E extends Elem<E>> {

    String getSimpleName();

    ElementKind getKind();

    boolean isAbstract();

    Elem<?> getEnclosingElement();

    E withEnclosingElement(Elem<?> enclosingElement);

    List<Elem<?>> getEnclosedElements();

    E withEnclosedElement(Elem<?> enclosedElement);

    E withEnclosedElements(List<Elem<?>> enclosedElements);

    Set<Modifier> getModifiers();

    E withModifier(Modifier modifier);

    default E withModifiers(final Modifier... modifiers) {
        for (final Modifier modifier : modifiers) {
            withModifier(modifier);
        }
        return (E) this;
    }

    default E withModifiers(final Collection<Modifier> modifiers) {
        modifiers.forEach(this::withModifier);
        return (E) this;
    }

    E withAnnotation(Annot a);

    default E withAnnotations(final Annot... annotations) {
        for (Annot annotation : annotations) {
            withAnnotation(annotation);
        }
        return (E) this;
    }

    <P, R> R accept(ElementVisitor<P, R> visitor, P p);
}
