package io.github.potjerodekool.codegen.template.model.type;

import io.github.potjerodekool.codegen.model.type.TypeKind;
import io.github.potjerodekool.codegen.template.model.expression.ExpressionKind;
import io.github.potjerodekool.codegen.template.model.expression.ExpressionVisitor;

public class NoTypeExpr implements TypeExpr {

    private final TypeKind typeKind;

    private NoTypeExpr(final TypeKind typeKind) {
        this.typeKind = typeKind;
    }

    public static NoTypeExpr createVoidType() {
        return new NoTypeExpr(TypeKind.VOID);
    }

    @Override
    public ExpressionKind getKind() {
        return ExpressionKind.TYPE;
    }

    @Override
    public TypeKind getTypeKind() {
        return typeKind;
    }

    @Override
    public <P, R> R accept(final ExpressionVisitor<P, R> visitor, final P p) {
        return visitor.visitNoTypeExpression(this, p);
    }
}
