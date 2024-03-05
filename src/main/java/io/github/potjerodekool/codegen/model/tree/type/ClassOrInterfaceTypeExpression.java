package io.github.potjerodekool.codegen.model.tree.type;

import io.github.potjerodekool.codegen.model.element.Name;
import io.github.potjerodekool.codegen.model.tree.TreeVisitor;
import io.github.potjerodekool.codegen.model.tree.expression.AbstractExpression;
import io.github.potjerodekool.codegen.model.type.TypeKind;
import io.github.potjerodekool.codegen.template.model.type.ClassOrInterfaceTypeExpr;

import java.util.ArrayList;
import java.util.List;

public class ClassOrInterfaceTypeExpression extends AbstractExpression implements TypeExpression {

    private Name name;

    private List<TypeExpression> typeArguments;

    private boolean isNullable;

    public ClassOrInterfaceTypeExpression(final String name) {
        this(Name.of(name));
    }

    public ClassOrInterfaceTypeExpression(final String name,
                                          final List<TypeExpression> typeArguments) {
        this(Name.of(name), typeArguments);
    }

    public ClassOrInterfaceTypeExpression(final Name name) {
        this(name, null);
    }

    public ClassOrInterfaceTypeExpression(final Name name,
                                          final List<TypeExpression> typeArguments) {
        this.name = name;
        this.typeArguments = typeArguments != null
                ? new ArrayList<>(typeArguments)
                : null;
    }

    public Name getName() {
        return name;
    }

    public ClassOrInterfaceTypeExpression name(final Name name) {
        this.name = name;
        return this;
    }

    @Override
    public <R, P> R accept(final TreeVisitor<R, P> visitor, final P param) {
        return visitor.visitClassOrInterfaceTypeExpression(this, param);
    }

    @Override
    public boolean isNullable() {
        return isNullable;
    }

    public void setNullable(final boolean nullable) {
        isNullable = nullable;
    }

    @Override
    public TypeKind getKind() {
        return TypeKind.DECLARED;
    }

    public List<? extends TypeExpression> getTypeArguments() {
        return typeArguments;
    }

    public ClassOrInterfaceTypeExpression typeArgument(final TypeExpression typeArgument) {
        if (this.typeArguments == null) {
            this.typeArguments = new ArrayList<>();
        }
        this.typeArguments.add(typeArgument);
        return this;
    }

    public ClassOrInterfaceTypeExpression typeArguments(final TypeExpression... typeArguments) {
        for (final TypeExpression typeArgument : typeArguments) {
            typeArgument(typeArgument);
        }
        return this;
    }

    @Override
    public TypeExpression asNonNullableType() {
        setNullable(false);
        return this;
    }
}
