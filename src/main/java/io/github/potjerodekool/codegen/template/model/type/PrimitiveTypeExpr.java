package io.github.potjerodekool.codegen.template.model.type;

import io.github.potjerodekool.codegen.model.type.TypeKind;
import io.github.potjerodekool.codegen.template.model.expression.ExpressionKind;
import io.github.potjerodekool.codegen.template.model.expression.ExpressionVisitor;

public class PrimitiveTypeExpr implements TypeExpr {

    private final String name;
    private final TypeKind typeKind;

    public PrimitiveTypeExpr(final TypeKind typeKind) {
        if (!typeKind.isPrimitive()) {
            throw new UnsupportedOperationException(String.format("%s is not a primitive type", typeKind));
        }

        this.name = switch (typeKind) {
            case BOOLEAN -> "boolean";
            case BYTE -> "byte";
            case SHORT -> "short";
            case INT ->  "int";
            case LONG -> "long";
            case CHAR -> "char";
            case FLOAT -> "float";
            case DOUBLE -> "double";
            default -> throw new UnsupportedOperationException(String.format("%s is not a primitive type", typeKind));
        };
        this.typeKind = typeKind;
    }

    public String getName() {
        return name;
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
        return visitor.visitPrimitiveTypeExpression(this, p );
    }
}
