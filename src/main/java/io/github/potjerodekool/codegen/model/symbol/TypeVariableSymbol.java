package io.github.potjerodekool.codegen.model.symbol;

import io.github.potjerodekool.codegen.model.element.*;
import io.github.potjerodekool.codegen.model.type.TypeMirror;
import io.github.potjerodekool.codegen.model.type.TypeVariableImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class TypeVariableSymbol extends TypeSymbol implements TypeParameterElement {

    public TypeVariableSymbol(final Name simpleName) {
        super(ElementKind.TYPE_PARAMETER, simpleName, new ArrayList<>());
        setType(new TypeVariableImpl(this));
    }

    @Override
    public ElementKind getKind() {
        return ElementKind.TYPE_PARAMETER;
    }

    @Override
    public TypeVariableImpl asType() {
        return (TypeVariableImpl) super.asType();
    }

    @Override
    public Element getGenericElement() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<? extends TypeMirror> getBounds() {
        final var upperBound = asType().getUpperBound();

        if (upperBound != null) {
            return List.of(upperBound);
        } else {
            return List.of();
        }
    }

    @Override
    public <R, P> R accept(final ElementVisitor<R, P> v, final P p) {
        return v.visitTypeParameter(this, p);
    }

    @Override
    public String toString() {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(getSimpleName());

        if (!getBounds().isEmpty()) {
            stringBuilder.append(" extends ");
            stringBuilder.append(getBounds().stream()
                    .map(Objects::toString)
                    .collect(Collectors.joining(","))
            );
        }

        return stringBuilder.toString();
    }

    @Override
    public Set<? extends Modifier> getModifiers() {
        return Set.of();
    }
}
