package io.github.potjerodekool.codegen.model.tree.expression;

public interface ExpressionBuilder<E extends Expression> {

    E build();

    static Expression expression(final ExpressionBuilder<? extends Expression> builder) {
        return builder.build();
    }

}
