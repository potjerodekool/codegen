package io.github.potjerodekool.codegen.model.symbol;

import io.github.potjerodekool.codegen.model.element.*;
import io.github.potjerodekool.codegen.model.tree.statement.BlockStatement;
import io.github.potjerodekool.codegen.model.type.ExecutableType;
import io.github.potjerodekool.codegen.model.type.TypeMirror;
import io.github.potjerodekool.codegen.model.type.java.JavaExecutableType;
import io.github.potjerodekool.codegen.model.type.java.JavaVoidType;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;

public class MethodSymbol extends AbstractSymbol<MethodSymbol> implements ExecutableElement {

    private TypeMirror returnType;
    private ExecutableType type;
    private final List<TypeParameterElement> typeParameters = new ArrayList<>();

    private final List<VariableSymbol> parameters = new ArrayList<>();
    private @Nullable BlockStatement body;

    private @Nullable AnnotationValue defaultValue = null;

    @SuppressWarnings("initialization.fields.uninitialized")
    private MethodSymbol(final ElementKind kind,
                         final TypeMirror returnType,
                         final Name simpleName) {
        super(kind, simpleName);
        this.returnType = returnType;
    }

    public static MethodSymbol create(final ElementKind kind,
                                      final List<AnnotationMirror> annotations,
                                      final Set<Modifier> modifiers,
                                      final TypeMirror returnType,
                                      final String simpleName,
                                      final List<VariableSymbol> parameters,
                                      final @Nullable BlockStatement body) {
        final var methodElement = new MethodSymbol(kind, returnType, Name.of(simpleName));
        methodElement.addModifiers(modifiers);
        methodElement.addAnnotations(annotations);
        parameters.forEach(methodElement::addParameter);
        methodElement.setBody(body);
        return methodElement;
    }

    public static MethodSymbol createConstructor(final String simpleName) {
        return createMethod(ElementKind.CONSTRUCTOR, JavaVoidType.INSTANCE, Name.of(simpleName));
    }

    public static MethodSymbol createConstructor(final Name simpleName) {
        return createMethod(ElementKind.CONSTRUCTOR, JavaVoidType.INSTANCE, simpleName);
    }

    public static MethodSymbol createMethod(final String simpleName) {
        return createMethod(Name.of(simpleName));
    }

    public static MethodSymbol createMethod(final Name simpleName) {
        return createMethod(ElementKind.METHOD, JavaVoidType.INSTANCE, simpleName);
    }

    public static MethodSymbol createMethod(final String simpleName,
                                            final TypeMirror returnType) {
        return createMethod(Name.of(simpleName), returnType);
    }

    public static MethodSymbol createMethod(final Name simpleName,
                                            final TypeMirror returnType) {
        return createMethod(ElementKind.METHOD, returnType, simpleName);
    }

    private static MethodSymbol createMethod(final ElementKind elementKind,
                                             final TypeMirror returnType,
                                             final Name simpleName) {
        final var me =  new MethodSymbol(elementKind, returnType, simpleName);
        final var type = new JavaExecutableType();
        me.setType(type);
        return me;
    }

    @Override
    public ExecutableType asType() {
        return type;
    }

    public void setType(final ExecutableType type) {
        this.type = type;
    }

    public TypeMirror getReturnType() {
        return returnType;
    }

    public List<? extends VariableSymbol> getParameters() {
        return parameters;
    }

    @Override
    public TypeMirror getReceiverType() {
        return null;
    }

    @Override
    public boolean isVarArgs() {
        return false;
    }

    @Override
    public boolean isDefault() {
        return false;
    }

    @Override
    public List<? extends TypeMirror> getThrownTypes() {
        return null;
    }

    public void addParameter(final VariableSymbol parameter) {
        this.parameters.add(parameter);
    }

    @Override
    public <R, P> R accept(final ElementVisitor<R, P> v, final P p) {
        return v.visitExecutable(this, p);
    }

    public Optional<BlockStatement> getBody() {
        return Optional.ofNullable(body);
    }

    public void setBody(final @Nullable BlockStatement body) {
        this.body = body;
    }

    public void setReturnType(final TypeMirror returnType) {
        this.returnType = returnType;
    }

    @Override
    public @Nullable AnnotationValue getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(final AnnotationValue defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public String toString() {
        return String.format("%s %s()", returnType, getSimpleName());
    }

    @Override
    public List<? extends TypeParameterElement> getTypeParameters() {
        return Collections.unmodifiableList(typeParameters);
    }
}
