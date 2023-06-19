package io.github.potjerodekool.codegen.model.tree.type;

import io.github.potjerodekool.codegen.model.tree.TreeVisitor;
import io.github.potjerodekool.codegen.model.tree.expression.AbstractExpression;
import io.github.potjerodekool.codegen.model.type.TypeKind;

public class VarTypeExpression extends AbstractExpression implements TypeExpression {

    @Override
    public boolean isNullable() {
        return false;
    }

    @Override
    public TypeKind getKind() {
        return null;
    }

    @Override
    public <R, P> R accept(final TreeVisitor<R, P> visitor,
                           final P param) {
        return visitor.visitVarTypeExpression(this, param);
    }
}
