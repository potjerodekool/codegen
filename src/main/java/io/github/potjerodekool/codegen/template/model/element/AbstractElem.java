package io.github.potjerodekool.codegen.template.model.element;

import io.github.potjerodekool.codegen.model.element.ElementKind;
import io.github.potjerodekool.codegen.model.element.Modifier;
import io.github.potjerodekool.codegen.template.model.annotation.Annot;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractElem<E extends AbstractElem<E>> implements Elem<E> {

    private String simpleName;

    private ElementKind kind;

    private Elem<?> enclosingElement;

    private final List<Elem<?>> enclosedElements = new ArrayList<>();

    private final Set<Modifier> modifiers = new LinkedHashSet<>();

    private final List<Annot> annotations = new ArrayList<>();

    public AbstractElem() {
    }

    @Override
    public String getSimpleName() {
        return simpleName;
    }

    public E simpleName(final String simpleName) {
        this.simpleName = simpleName;
        return (E) this;
    }

    public ElementKind getKind() {
        return kind;
    }

    public E kind(final ElementKind kind) {
        this.kind = kind;
        return (E) this;
    }

    @Override
    public Elem<?> getEnclosingElement() {
        return enclosingElement;
    }

    @Override
    public E enclosingElement(final Elem<?> element) {
        this.enclosingElement = element;
        return (E) this;
    }

    @Override
    public List<Elem<?>> getEnclosedElements() {
        return enclosedElements;
    }

    @Override
    public E enclosedElement(final Elem<?> enclosedElement) {
        this.enclosedElements.add(enclosedElement);
        return (E) this;
    }

    @Override
    public E enclosedElements(final List<Elem<?>> enclosedElements) {
        this.enclosedElements.addAll(enclosedElements);
        return (E) this;
    }

    @Override
    public Set<Modifier> getModifiers() {
        return modifiers;
    }

    @Override
    public E modifier(final Modifier modifier) {
        this.modifiers.add(modifier);
        return (E) this;
    }

    @Override
    public boolean isAbstract() {
        return modifiers.contains(Modifier.ABSTRACT);
    }

    public List<Annot> getAnnotations() {
        return annotations;
    }

    @Override
    public E annotation(final Annot a) {
        annotations.add(a);
        return (E) this;
    }
}
