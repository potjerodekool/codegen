package io.github.potjerodekool.codegen.model.tree.type;

import io.github.potjerodekool.codegen.model.tree.expression.Expression;
import io.github.potjerodekool.codegen.model.type.TypeKind;

public interface TypeExpression extends Expression {

    default TypeExpression asNonNullableType() {
        throw new UnsupportedOperationException();
    }

    boolean isNullable();

    TypeKind getKind();

}
