package io.github.potjerodekool.codegen.template.model.type;

import io.github.potjerodekool.codegen.model.type.TypeKind;
import io.github.potjerodekool.codegen.template.model.expression.ExpressionKind;
import io.github.potjerodekool.codegen.template.model.expression.ExpressionVisitor;

public class TypeVarExpr implements TypeExpr  {

    private String name;
    private TypeExpr bounds;

    public String getName() {
        return name;
    }

    public TypeVarExpr name(final String name) {
        this.name = name;
        return this;
    }

    public TypeExpr getBounds() {
        return bounds;
    }

    public TypeVarExpr bounds(final TypeExpr bounds) {
        this.bounds = bounds;
        return this;
    }

    @Override
    public ExpressionKind getKind() {
        return ExpressionKind.TYPE;
    }

    @Override
    public <P, R> R accept(final ExpressionVisitor<P, R> visitor, final P p) {
        return visitor.visitTypeVarExpression(this, p);
    }

    @Override
    public TypeKind getTypeKind() {
        return TypeKind.TYPEVAR;
    }
}
