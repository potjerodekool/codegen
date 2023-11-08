package io.github.potjerodekool.codegen.model.tree.type;

import io.github.potjerodekool.codegen.model.tree.TreeVisitor;
import io.github.potjerodekool.codegen.model.tree.expression.AbstractExpression;
import io.github.potjerodekool.codegen.model.type.TypeKind;

public class PrimitiveTypeExpression extends AbstractExpression implements TypeExpression {

    private final TypeKind kind;

    private PrimitiveTypeExpression(final TypeKind kind) {
        this.kind = kind;
    }

    public static PrimitiveTypeExpression booleanTypeExpression() {
        return new PrimitiveTypeExpression(TypeKind.BOOLEAN);
    }

    public static PrimitiveTypeExpression byteTypeExpression() {
        return new PrimitiveTypeExpression(TypeKind.BYTE);
    }

    public static PrimitiveTypeExpression shortTypeExpression() {
        return new PrimitiveTypeExpression(TypeKind.SHORT);
    }

    public static PrimitiveTypeExpression intTypeExpression() {
        return new PrimitiveTypeExpression(TypeKind.INT);
    }

    public static PrimitiveTypeExpression longTypeExpression() {
        return new PrimitiveTypeExpression(TypeKind.LONG);
    }

    public static PrimitiveTypeExpression charTypeExpression() {
        return new PrimitiveTypeExpression(TypeKind.CHAR);
    }

    public static PrimitiveTypeExpression floatTypeExpression() {
        return new PrimitiveTypeExpression(TypeKind.FLOAT);
    }

    public static PrimitiveTypeExpression doubleTypeExpression() {
        return new PrimitiveTypeExpression(TypeKind.DOUBLE);
    }

    public TypeKind getKind() {
        return kind;
    }

    @Override
    public boolean isNullable() {
        return false;
    }

    @Override
    public <R, P> R accept(final TreeVisitor<R, P> visitor, final P param) {
        return visitor.visitPrimitiveTypeExpression(this, param);
    }

    @Override
    public TypeExpression asNonNullableType() {
        return this;
    }
}
