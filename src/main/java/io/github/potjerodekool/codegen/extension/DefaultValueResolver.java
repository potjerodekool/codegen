package io.github.potjerodekool.codegen.extension;

import io.github.potjerodekool.codegen.model.tree.expression.Expression;
import io.github.potjerodekool.codegen.model.type.TypeMirror;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface DefaultValueResolver {

    @Nullable Expression createDefaultValue(TypeMirror typeMirror);
}
