package io.github.potjerodekool.codegen.template.model.type;

import io.github.potjerodekool.codegen.model.type.TypeKind;
import io.github.potjerodekool.codegen.template.model.expression.ExpressionKind;
import io.github.potjerodekool.codegen.template.model.expression.ExpressionVisitor;

public class VarTypeExp implements TypeExpr {
    @Override
    public ExpressionKind getKind() {
        return ExpressionKind.TYPE;
    }

    @Override
    public <P, R> R accept(final ExpressionVisitor<P, R> visitor, final P p) {
        return visitor.visitVarTypeExpression(this, p);
    }

    @Override
    public TypeKind getTypeKind() {
        return TypeKind.VAR;
    }
}
