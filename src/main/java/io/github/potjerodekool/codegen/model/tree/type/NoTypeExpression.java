package io.github.potjerodekool.codegen.model.tree.type;

import io.github.potjerodekool.codegen.model.tree.TreeVisitor;
import io.github.potjerodekool.codegen.model.tree.expression.AbstractExpression;
import io.github.potjerodekool.codegen.model.type.TypeKind;

public class NoTypeExpression extends AbstractExpression implements TypeExpression {

    private final TypeKind kind;

    public NoTypeExpression(final TypeKind kind) {
        this.kind = kind;
    }

    @Override
    public TypeKind getKind() {
        return kind;
    }

    @Override
    public boolean isNullable() {
        return false;
    }

    @Override
    public <R, P> R accept(final TreeVisitor<R, P> visitor, final P param) {
        return visitor.visitNoType(this, param);
    }
}
