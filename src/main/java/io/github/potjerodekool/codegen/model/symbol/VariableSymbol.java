package io.github.potjerodekool.codegen.model.symbol;

import io.github.potjerodekool.codegen.model.element.*;
import io.github.potjerodekool.codegen.model.type.TypeMirror;

import java.util.List;
import java.util.Set;

public class VariableSymbol extends AbstractSymbol<VariableSymbol> implements VariableElement {

    private VariableSymbol(final ElementKind kind,
                           final Name simpleName) {
        super(kind, simpleName);
    }

    public static VariableSymbol create(final ElementKind kind,
                                        final TypeMirror type,
                                        final Name simpleName,
                                        final List<AnnotationMirror> annotations,
                                        final Set<Modifier> modifiers) {
        final var variableElement = new VariableSymbol(kind, simpleName);
        variableElement.setType(type);
        variableElement.addAnnotations(annotations);
        variableElement.addModifiers(modifiers);
        return variableElement;
    }

    public static VariableSymbol createParameter(final String simpleName,
                                                 final TypeMirror type) {
        final var ve = new VariableSymbol(
                ElementKind.PARAMETER,
                Name.of(simpleName)
        );
        ve.setType(type);
        return ve;
    }

    public static VariableSymbol createLocalVariable(final String simpleName,
                                                     final TypeMirror type) {
        final var ve = new VariableSymbol(
                ElementKind.LOCAL_VARIABLE,
                Name.of(simpleName)
        );
        ve.setType(type);
        return ve;
    }

    public static VariableSymbol createField(final String simpleName,
                                             final TypeMirror type) {
        return createField(Name.of(simpleName), type);
    }

    public static VariableSymbol createField(final Name simpleName,
                                             final TypeMirror type) {
        final var ve = new VariableSymbol(
                ElementKind.FIELD,
                simpleName
        );
        ve.setType(type);
        return ve;
    }

    @Override
    public <R, P> R accept(final ElementVisitor<R, P> v, final P p) {
        return v.visitVariable(this, p);
    }

    @Override
    public Object getConstantValue() {
        return null;
    }

}