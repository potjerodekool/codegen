package io.github.potjerodekool.codegen.model.symbol;

import io.github.potjerodekool.codegen.model.AstNode;
import io.github.potjerodekool.codegen.model.Attribute;
import io.github.potjerodekool.codegen.model.Completer;
import io.github.potjerodekool.codegen.model.DummyCompleter;
import io.github.potjerodekool.codegen.model.element.*;
import io.github.potjerodekool.codegen.model.type.DeclaredType;
import io.github.potjerodekool.codegen.model.type.TypeMirror;
import io.github.potjerodekool.codegen.model.util.Elements;
import io.github.potjerodekool.codegen.resolve.WritableScope;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.annotation.Annotation;
import java.util.*;

@SuppressWarnings("initialization.fields.uninitialized")
public abstract class AbstractSymbol implements AstNode, Element {

    private ElementKind kind;

    private Name simpleName;

    private final Set<Modifier> modifiers = new HashSet<>();

    private TypeMirror type;

    private @Nullable Element enclosingElement;

    private final List<Element> enclosedElements = new ArrayList<>();

    private final List<AnnotationMirror> annotations = new ArrayList<>();

    private Completer completer = DummyCompleter.INSTANCE;

    @SuppressWarnings("initialization.fields.uninitialized")
    protected AbstractSymbol(final ElementKind kind,
                             final CharSequence simpleName) {
        this(kind, simpleName, new ArrayList<>());
    }

    @SuppressWarnings("method.invocation")
    protected AbstractSymbol(final ElementKind kind,
                             final CharSequence simpleName,
                             final List<AnnotationMirror> annotations) {
        this.kind = kind;
        validateSimpleName(simpleName);
        this.simpleName = Name.of(simpleName);
        this.annotations.addAll(annotations);
    }

    @Override
    public ElementKind getKind() {
        return kind;
    }

    public void setKind(final ElementKind kind) {
        this.kind = kind;
    }

    public void setSimpleName(final Name simpleName) {
        validateSimpleName(simpleName);
        this.simpleName = simpleName;
    }

    protected void validateSimpleName(final CharSequence simpleName) {
        if (simpleName.toString().contains(".")) {
            throw new IllegalArgumentException("Not a simpleName " + simpleName);
        }
    }

    public void setType(final TypeMirror type) {
        this.type = type;
    }

    @Override
    public Name getSimpleName() {
        return simpleName;
    }

    public Name getQualifiedName() {
        return simpleName;
    }

    @Override
    public Set<? extends Modifier> getModifiers() {
        return modifiers;
    }

    public void addModifiers(final Set<Modifier> modifiers) {
        this.modifiers.addAll(modifiers);
    }

    @Override
    public TypeMirror asType() {
        return type;
    }

    @Override
    public @Nullable Element getEnclosingElement() {
        return enclosingElement;
    }

    public void setEnclosingElement(final @Nullable Element enclosingElement) {
        this.enclosingElement = enclosingElement;
    }

    @Override
    public List<Element> getEnclosedElements() {
        return Collections.unmodifiableList(this.enclosedElements);
    }

    public void addEnclosedElement(final Element enclosedElement) {
        this.enclosedElements.add(enclosedElement);
    }

    public void removeEnclosedElement(final Element enclosedElement) {
        this.enclosedElements.remove(enclosedElement);
    }

    @Override
    public List<? extends AnnotationMirror> getAnnotationMirrors() {
        return Collections.unmodifiableList(this.annotations);
    }

    public boolean isAnnotationPresent(final String className) {
        return this.annotations.stream()
                .map(AnnotationMirror::getAnnotationType)
                .map(DeclaredType::asElement)
                .anyMatch(it -> className.equals(Elements.getQualifiedName(it).toString()));
    }

    @Override
    public <A extends Annotation> A[] getAnnotationsByType(final Class<A> annotationType) {
        return null;
    }

    @Override
    public <A extends Annotation> A getAnnotation(final Class<A> annotationType) {
        return null;
    }

    public void addAnnotation(final ClassSymbol classSymbol) {
        addAnnotation(Attribute.compound(classSymbol));
    }

    public void addAnnotation(final ClassSymbol classSymbol,
                              final AnnotationValue expression) {
        final var annotation = Attribute.compound(classSymbol);
        annotation.addElementValue(Name.of("value"), expression);
        addAnnotation(annotation);
    }

    public void addAnnotation(final AnnotationMirror annotation) {
        this.annotations.add(annotation);
    }

    public void addAnnotations(final List<AnnotationMirror> annotations) {
        this.annotations.addAll(annotations);
    }

    public Completer getCompleter() {
        return completer;
    }

    public void setCompleter(final Completer completer) {
        this.completer = completer;
    }

    public void removeModifier(final Modifier modifier) {
        this.modifiers.remove(modifier);
    }

    public void addModifiers(final Modifier... modifiers) {
        this.modifiers.addAll(List.of(modifiers));
    }

    public WritableScope members() {
        return null;
    }
}
