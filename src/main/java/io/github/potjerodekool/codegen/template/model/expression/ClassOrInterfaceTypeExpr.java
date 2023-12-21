package io.github.potjerodekool.codegen.template.model.expression;

public class ClassOrInterfaceTypeExpr implements TypeExpr {

    private String name;

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

    @Override
    public ExpressionKind getKind() {
        return ExpressionKind.CLASS_OR_INTERFACE;
    }

    @Override
    public <P, R> R accept(final ExpressionVisitor<P, R> visitor, final P p) {
        return visitor.visitClassOrInterfaceTypeExpression(this, p);
    }
}
