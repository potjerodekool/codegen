package io.github.potjerodekool.codegen.model.symbol;

import io.github.potjerodekool.codegen.model.element.*;
import io.github.potjerodekool.codegen.model.tree.statement.BlockStatement;
import io.github.potjerodekool.codegen.model.type.ExecutableType;
import io.github.potjerodekool.codegen.model.type.TypeMirror;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MethodSymbol extends AbstractSymbol implements ExecutableElement {

    private TypeMirror returnType;
    private ExecutableType type;
    private final List<TypeParameterElement> typeParameters = new ArrayList<>();
    private final List<VariableSymbol> parameters = new ArrayList<>();

    private @Nullable BlockStatement body;

    private @Nullable AnnotationValue defaultValue = null;

    @SuppressWarnings("initialization.fields.uninitialized")
    public MethodSymbol(final ElementKind kind,
                        final TypeMirror returnType,
                        final CharSequence simpleName) {
        super(kind, simpleName);
        this.returnType = returnType;
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

    public void addParameters(final List<VariableSymbol> parameters) {
        this.parameters.addAll(parameters);
    }

    public void addParameter(final VariableSymbol parameter) {
        this.parameters.add(parameter);
    }
}
