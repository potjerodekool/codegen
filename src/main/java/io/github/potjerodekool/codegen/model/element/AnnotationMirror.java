package io.github.potjerodekool.codegen.model.element;

import io.github.potjerodekool.codegen.model.AstNode;
import io.github.potjerodekool.codegen.model.type.DeclaredType;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Map;

public interface AnnotationMirror extends AstNode {

    DeclaredType getAnnotationType();

    Map<? extends ExecutableElement, ? extends AnnotationValue> getElementValues();

    <R,P> R accept(AnnotationValueVisitor<R,P> visitor, P param);

    default @Nullable AnnotationTarget getTarget() {
        return null;
    }

}
