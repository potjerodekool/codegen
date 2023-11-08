package io.github.potjerodekool.codegen.model.symbol;

import io.github.potjerodekool.codegen.model.element.AnnotationMirror;
import io.github.potjerodekool.codegen.model.element.ElementKind;

import java.util.List;

public abstract class TypeSymbol extends AbstractSymbol {

    protected TypeSymbol(final ElementKind kind,
                         final CharSequence simpleName,
                         final List<AnnotationMirror> annotations) {
        super(kind, simpleName, annotations);
    }
}
