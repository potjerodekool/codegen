package io.github.potjerodekool.codegen.model.tree.expression;

import io.github.potjerodekool.codegen.model.tree.TreeVisitor;

import java.util.ArrayList;
import java.util.List;

public class ArrayInitializerExpression extends AbstractExpression {

    private final List<Expression> values = new ArrayList<>();

    public ArrayInitializerExpression() {
        this(List.of());
    }

    public <E extends Expression> ArrayInitializerExpression(final E value) {
        this(List.of(value));
    }

    public ArrayInitializerExpression(final List<? extends Expression> values) {
        this.values.addAll(values);
    }

    public List<Expression> getValues() {
        return values;
    }

    @Override
    public <R, P> R accept(final TreeVisitor<R, P> visitor, final P param) {
        return visitor.visitArrayInitializerExpression(this, param);
    }
}
