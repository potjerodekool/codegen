package io.github.potjerodekool.codegen.model.symbol;

import io.github.potjerodekool.codegen.model.element.*;
import io.github.potjerodekool.codegen.model.type.TypeMirror;
import io.github.potjerodekool.codegen.resolve.Scope;
import io.github.potjerodekool.codegen.resolve.WritableScope;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ClassSymbol extends TypeSymbol implements TypeElement {

    private @Nullable TypeMirror superType = null;

    private final List<TypeMirror> interfaces = new ArrayList<>();

    private @Nullable MethodSymbol primaryConstructor;

    private final List<TypeParameterElement> typeParameters = new ArrayList<>();

    private NestingKind nestingKind;

    private Name qualifiedName;

    public WritableScope scope;

    public ClassSymbol(final ElementKind kind,
                          final CharSequence simpleName,
                          final NestingKind nestingKind,
                          final AbstractSymbol enclosingElement) {
        super(kind, simpleName, new ArrayList<>());
        this.nestingKind = nestingKind;
        setEnclosingElement(enclosingElement);
    }

    @Override
    public void setEnclosingElement(final @Nullable Element enclosingElement) {
        super.setEnclosingElement(enclosingElement);
        setQualifiedName(createQualifiedName(getSimpleName(), enclosingElement));
    }

    private Name createQualifiedName(final CharSequence simpleName,
                                     final Element enclosingElement) {
        if (enclosingElement == null || isDefaultPackage(enclosingElement)) {
            return Name.of(simpleName);
        } else {
            final Name parentName;

            if (enclosingElement instanceof QualifiedNameable qualifiedNameable) {
                parentName = qualifiedNameable.getQualifiedName();
            } else {
                parentName = enclosingElement.getSimpleName();
            }

            return parentName.append(simpleName);
        }
    }

    private boolean isDefaultPackage(final Element element) {
        return element instanceof PackageElement packageElement
                && packageElement.isUnnamed();
    }

    @Override
    public @Nullable TypeMirror getSuperclass() {
        return superType;
    }

    public void setSuperType(final @Nullable TypeMirror superType) {
        this.superType = superType;
    }

    public List<? extends TypeMirror> getInterfaces() {
        return Collections.unmodifiableList(interfaces);
    }

    @Override
    public List<? extends TypeParameterElement> getTypeParameters() {
        return Collections.unmodifiableList(typeParameters);
    }

    public void addTypeParameter(final TypeParameterElement typeParameter) {
        this.typeParameters.add(typeParameter);
    }

    public void addInterface(final TypeMirror interfaceType) {
        this.interfaces.add(interfaceType);
    }

    public @Nullable MethodSymbol getPrimaryConstructor() {
        return primaryConstructor;
    }

    protected void setPrimaryConstructor(final MethodSymbol primaryConstructor) {
        this.primaryConstructor = primaryConstructor;
    }

    @Override
    public NestingKind getNestingKind() {
        return nestingKind;
    }

    public void setNestingKind(final NestingKind nestingKind) {
        this.nestingKind = nestingKind;
    }

    @Override
    public Name getQualifiedName() {
        return qualifiedName;
    }

    public void setQualifiedName(final Name qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    @Override
    public <R, P> R accept(final ElementVisitor<R, P> v, final P p) {
        return v.visitType(this, p);
    }

    @Override
    public String toString() {
        final var stringBuilder  = new StringBuilder();
        stringBuilder.append(getQualifiedName());

        if (!typeParameters.isEmpty()) {
            stringBuilder.append("<");

            stringBuilder.append(typeParameters.stream()
                            .map(Object::toString)
                                    .collect(Collectors.joining(", "))
            );
            stringBuilder.append(">");
        }

        if (superType != null) {
            stringBuilder.append(" extends ").append(superType);
        }

        if (!interfaces.isEmpty()) {
            if (getKind() == ElementKind.CLASS) {
                stringBuilder.append(" implements ");
            } else {
                stringBuilder.append(" extends ");
            }
            stringBuilder.append(interfaces.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(", "))
            );
        }

        return stringBuilder.toString();
    }

    @Override
    public WritableScope members() {
        return scope;
    }
}
