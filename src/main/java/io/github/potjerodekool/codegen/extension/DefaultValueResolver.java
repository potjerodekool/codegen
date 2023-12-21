package io.github.potjerodekool.codegen.extension;

import io.github.potjerodekool.codegen.model.tree.expression.Expression;
import io.github.potjerodekool.codegen.model.type.TypeMirror;

public interface DefaultValueResolver {

    Expression createDefaultValue(TypeMirror typeMirror);
}
