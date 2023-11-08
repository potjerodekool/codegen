package io.github.potjerodekool.codegen.model.tree.type;

import io.github.potjerodekool.codegen.model.element.Name;
import io.github.potjerodekool.codegen.model.tree.TreeVisitor;
import io.github.potjerodekool.codegen.model.tree.expression.AbstractExpression;
import io.github.potjerodekool.codegen.model.type.TypeKind;

import java.util.ArrayList;
import java.util.List;

public class ClassOrInterfaceTypeExpression extends AbstractExpression implements TypeExpression {

    private final Name name;

    private final List<TypeExpression> typeArguments;

    private boolean isNullable;

    public ClassOrInterfaceTypeExpression(final String name) {
        this(Name.of(name));
    }

    public ClassOrInterfaceTypeExpression(final String name,
                                          final List<TypeExpression> typeArguments) {
        this(Name.of(name), typeArguments);
    }

    public ClassOrInterfaceTypeExpression(final Name name) {
        this(name, new ArrayList<>());
    }

    public ClassOrInterfaceTypeExpression(final Name name,
                                          final List<TypeExpression> typeArguments) {
        this.name = name;
        this.typeArguments = typeArguments;
    }

    public Name getName() {
        return name;
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

    public void addTypeArgument(final TypeExpression typeArgument) {
        this.typeArguments.add(typeArgument);
    }
}
