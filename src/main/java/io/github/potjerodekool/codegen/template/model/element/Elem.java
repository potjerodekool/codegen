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

    E enclosingElement(Elem<?> enclosingElement);

    List<Elem<?>> getEnclosedElements();

    E enclosedElement(Elem<?> enclosedElement);

    E enclosedElements(List<Elem<?>> enclosedElements);

    Set<Modifier> getModifiers();

    E modifier(Modifier modifier);

    default E modifiers(final Modifier... modifiers) {
        for (final Modifier modifier : modifiers) {
            modifier(modifier);
        }
        return (E) this;
    }

    default E modifiers(final Collection<Modifier> modifiers) {
        modifiers.forEach(this::modifier);
        return (E) this;
    }

    E annotation(Annot a);

    default E annotations(final Annot... annotations) {
        for (Annot annotation : annotations) {
            annotation(annotation);
        }
        return (E) this;
    }

    default E annotations(final List<Annot> annotations) {
        for (Annot annotation : annotations) {
            annotation(annotation);
        }
        return (E) this;
    }

    <P, R> R accept(ElementVisitor<P, R> visitor, P p);
}
