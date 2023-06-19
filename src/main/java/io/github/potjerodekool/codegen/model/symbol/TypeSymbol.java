package io.github.potjerodekool.codegen.model.symbol;

import io.github.potjerodekool.codegen.model.element.AnnotationMirror;
import io.github.potjerodekool.codegen.model.element.ElementKind;
import io.github.potjerodekool.codegen.model.element.Name;

import java.util.List;

public abstract class TypeSymbol<T extends TypeSymbol<T>> extends AbstractSymbol<T> {

    protected TypeSymbol(final ElementKind kind,
                         final Name simpleName,
                         final List<AnnotationMirror> annotations) {
        super(kind, simpleName, annotations);
    }
}
