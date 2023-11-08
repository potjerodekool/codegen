package io.github.potjerodekool.codegen.model.symbol;

import io.github.potjerodekool.codegen.model.element.*;
import io.github.potjerodekool.codegen.model.type.TypeMirror;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

public class ModuleSymbol implements ModuleElement {

    private final Name qualifiedName;
    private final Name simpleName;

    public static final ModuleSymbol UNNAMED = new ModuleSymbol(null);

    public ModuleSymbol(final Name qualifiedName) {
        this.qualifiedName = qualifiedName;
        this.simpleName = qualifiedName != null
                ? qualifiedName.shortName()
                : null;
    }

    @Override
    public ElementKind getKind() {
        return ElementKind.MODULE;
    }

    @Override
    public Set<? extends Modifier> getModifiers() {
        return Set.of();
    }

    @Override
    public List<? extends AnnotationMirror> getAnnotationMirrors() {
        return List.of();
    }

    @Override
    public <A extends Annotation> A getAnnotation(final Class<A> annotationType) {
        return null;
    }

    @Override
    public <A extends Annotation> A[] getAnnotationsByType(final Class<A> annotationType) {
        return null;
    }

    @Override
    public <R, P> R accept(final ElementVisitor<R, P> v, final P p) {
        return v.visitModule(this, p);
    }

    @Override
    public TypeMirror asType() {
        return null;
    }

    @Override
    public Name getQualifiedName() {
        return qualifiedName;
    }

    @Override
    public Name getSimpleName() {
        return simpleName;
    }

    @Override
    public List<? extends Element> getEnclosedElements() {
        return List.of();
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public boolean isUnnamed() {
        return this == UNNAMED;
    }

    @Override
    public Element getEnclosingElement() {
        return null;
    }

    @Override
    public List<? extends Directive> getDirectives() {
        return List.of();
    }
}
