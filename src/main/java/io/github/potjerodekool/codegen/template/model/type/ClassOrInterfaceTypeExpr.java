package io.github.potjerodekool.codegen.template.model.type;

import io.github.potjerodekool.codegen.template.model.expression.ExpressionKind;
import io.github.potjerodekool.codegen.template.model.expression.ExpressionVisitor;

import java.util.ArrayList;
import java.util.List;

public class ClassOrInterfaceTypeExpr implements TypeExpr {

    private String name;

    private List<TypeExpr> typeArguments;

    public ClassOrInterfaceTypeExpr(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ClassOrInterfaceTypeExpr name(final String name) {
        this.name = name;
        return this;
    }

    public List<TypeExpr> getTypeArguments() {
        return typeArguments;
    }

    public ClassOrInterfaceTypeExpr typeArgument(final TypeExpr typeArgument) {
        if (typeArguments == null) {
            typeArguments = new ArrayList<>();
        }
        typeArguments.add(typeArgument);
        return this;
    }

    public ClassOrInterfaceTypeExpr typeArguments(final TypeExpr... typeArguments) {
        for (final TypeExpr argument : typeArguments) {
            typeArgument(argument);
        }
        return this;
    }

    @Override
    public ExpressionKind getKind() {
        return ExpressionKind.CLASS_OR_INTERFACE;
    }

    @Override
    public <P, R> R accept(final ExpressionVisitor<P, R> visitor, final P p) {
        return visitor.visitClassOrInterfaceTypeExpression(this, p);
    }
}
