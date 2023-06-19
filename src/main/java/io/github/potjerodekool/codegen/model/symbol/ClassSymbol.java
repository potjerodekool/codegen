package io.github.potjerodekool.codegen.model.symbol;

import io.github.potjerodekool.codegen.model.element.*;
import io.github.potjerodekool.codegen.model.type.ClassType;
import io.github.potjerodekool.codegen.model.type.TypeMirror;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ClassSymbol extends TypeSymbol<ClassSymbol> implements TypeElement {

    private @Nullable TypeMirror superType = null;

    private final List<TypeMirror> interfaces = new ArrayList<>();

    private @Nullable MethodSymbol primaryConstructor;

    private final List<TypeParameterElement> typeParameters = new ArrayList<>();

    private final NestingKind nestingKind;

    private Name qualifiedName;

    ClassSymbol(final ElementKind kind,
                final Name simpleName,
                final NestingKind nestingKind,
                final Element enclosingElement) {
        super(kind, simpleName, new ArrayList<>());
        this.nestingKind = nestingKind;
        this.qualifiedName = simpleName;
        setEnclosingElement(enclosingElement);
    }

    public static ClassSymbol create(final ElementKind kind,
                                     final Name simpleName,
                                     final NestingKind nestingKind,
                                     final Element enclosingElement) {
        final var classSymbol = new ClassSymbol(kind, simpleName, nestingKind, enclosingElement);
        final var classType = new ClassType(classSymbol, false);
        classSymbol.setType(classType);
        return classSymbol;
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

    public MethodSymbol addPrimaryConstructor() {
        final var pc = MethodSymbol.createConstructor(getSimpleName());
        this.primaryConstructor = pc;
        return pc;
    }

    public MethodSymbol addConstructor(final Modifier... modifiers) {
        final var constructor = MethodSymbol.createConstructor(getSimpleName());
        constructor.addModifiers(modifiers);
        addEnclosedElement(constructor);
        return constructor;
    }

    public MethodSymbol addMethod(final String name,
                                  final Modifier... modifiers) {
        return addMethod(Name.of(name), modifiers);
    }

    public MethodSymbol addMethod(final Name name,
                                  final Modifier... modifiers) {
        final var constructor = MethodSymbol.createMethod(name);
        constructor.addModifiers(modifiers);
        addEnclosedElement(constructor);
        return constructor;
    }

    public MethodSymbol addMethod(final String name,
                                  final TypeMirror returnType,
                                  final Modifier... modifiers) {
        return addMethod(Name.of(name), returnType, modifiers);
    }

    public MethodSymbol addMethod(final Name name,
                                  final TypeMirror returnType,
                                  final Modifier... modifiers) {
        final var constructor = MethodSymbol.createMethod(name, returnType);
        constructor.addModifiers(modifiers);
        addEnclosedElement(constructor);
        return constructor;
    }

    public void addPrimaryConstructor(final MethodSymbol primaryConstructor) {
        this.primaryConstructor = primaryConstructor;
    }

    public @Nullable MethodSymbol getPrimaryConstructor() {
        return primaryConstructor;
    }

    @Override
    public NestingKind getNestingKind() {
        return nestingKind;
    }

    @Override
    public Name getQualifiedName() {
        return qualifiedName;
    }

    @Override
    public void setEnclosingElement(final @Nullable Element enclosingElement) {
        super.setEnclosingElement(enclosingElement);

        if (enclosingElement == null) {
            this.qualifiedName = getSimpleName();
        } else {
            final Name enclosingName;

            if (enclosingElement instanceof QualifiedNameable qualifiedNameable) {
                enclosingName = qualifiedNameable.getQualifiedName();
            } else {
                enclosingName = enclosingElement.getSimpleName();
            }

            this.qualifiedName = Name.of(enclosingName, getSimpleName());
            /*
            if (nestingKind == NestingKind.MEMBER) {
                this.qualifiedName = Name.createInnerClassName(enclosingName, getSimpleName());
            } else {
                this.qualifiedName = Name.of(enclosingName, getSimpleName());
            }
            */
        }
    }

    public VariableSymbol addField(final TypeMirror fieldType,
                                   final String name,
                                   final Modifier... modifiers) {
        return addField(fieldType, Name.of(name), modifiers);
    }

    public VariableSymbol addField(final TypeMirror fieldType,
                                   final Name name,
                                   final Modifier... modifiers) {
        final var field = VariableSymbol.createField(name, fieldType)
            .addModifiers(modifiers);
        addEnclosedElement(field);
        return field;
    }

    @Override
    public <R, P> R accept(final ElementVisitor<R, P> v, final P p) {
        return v.visitType(this, p);
    }

    @Override
    public String toString() {
        final var stringBuilder  = new StringBuilder();
        stringBuilder.append(getQualifiedName());

        if (typeParameters.size() > 0) {
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

        if (interfaces.size() > 0) {
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

}
