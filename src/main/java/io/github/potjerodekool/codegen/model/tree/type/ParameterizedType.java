package io.github.potjerodekool.codegen.model.tree.type;

import io.github.potjerodekool.codegen.model.tree.TreeVisitor;
import io.github.potjerodekool.codegen.model.tree.expression.AbstractExpression;
import io.github.potjerodekool.codegen.model.tree.expression.Expression;
import io.github.potjerodekool.codegen.model.type.TypeKind;

import java.util.ArrayList;
import java.util.List;

public class ParameterizedType extends AbstractExpression implements TypeExpression {

    private final Expression clazz;

    private final List<Expression> arguments = new ArrayList<>();

    public ParameterizedType(final Expression clazz) {
        this(clazz, List.of());
    }

    public ParameterizedType(final Expression clazz,
                             final List<Expression> arguments) {
        this.clazz = clazz;
        this.arguments.addAll(arguments);
    }

    public Expression getClazz() {
        return clazz;
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    @Override
    public boolean isNullable() {
        return clazz instanceof TypeExpression typeExpression
                && typeExpression.isNullable();
    }

    @Override
    public TypeKind getKind() {
        return TypeKind.DECLARED;
    }

    @Override
    public <R, P> R accept(final TreeVisitor<R, P> visitor, final P param) {
        return visitor.visitParameterizedType(this, param);
    }

}
