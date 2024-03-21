package io.github.potjerodekool.codegen.template.model.type;

import io.github.potjerodekool.codegen.model.type.TypeKind;
import io.github.potjerodekool.codegen.template.model.annotation.Annot;
import io.github.potjerodekool.codegen.template.model.expression.ExpressionKind;
import io.github.potjerodekool.codegen.template.model.expression.ExpressionVisitor;

import java.util.ArrayList;
import java.util.List;

public class ClassOrInterfaceTypeExpr implements TypeExpr {

    private String name;

    private String packageName;

    private String simpleName;

    private List<TypeExpr> typeArguments;

    private List<Annot> annotations;

    public ClassOrInterfaceTypeExpr() {
    }

    public ClassOrInterfaceTypeExpr(final String name) {
        name(name);
    }

    public String getName() {
        return name;
    }

    public ClassOrInterfaceTypeExpr name(final String name) {
        final var packageNameEnd = name.lastIndexOf(".");

        if (packageNameEnd > 0) {
            packageName = name.substring(0, packageNameEnd);
            simpleName = name.substring(packageNameEnd + 1);
        } else {
            packageName = null;
            simpleName = name;
        }

        this.name = name;
        return this;
    }

    public String getPackageName() {
        return packageName;
    }

    public ClassOrInterfaceTypeExpr packageName(final String packageName) {
        this.packageName = packageName;
        this.name = simpleName != null? packageName + "." + simpleName: packageName;
        return this;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public ClassOrInterfaceTypeExpr simpleName(final String simpleName) {
        this.simpleName = simpleName;
        this.name = packageName != null ? packageName + "." + simpleName : simpleName;
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
    public ClassOrInterfaceTypeExpr typeArguments(final List<TypeExpr> typeArguments) {
        for (final TypeExpr argument : typeArguments) {
            typeArgument(argument);
        }
        return this;
    }


    public ClassOrInterfaceTypeExpr typeArguments(final TypeExpr... typeArguments) {
        for (final TypeExpr argument : typeArguments) {
            typeArgument(argument);
        }
        return this;
    }

    public List<Annot> getAnnotations() {
        return annotations;
    }

    public ClassOrInterfaceTypeExpr annotation(final Annot annotation) {
        if (annotations == null) {
            annotations = new ArrayList<>();
        }
        annotations.add(annotation);
        return this;
    }

    @Override
    public ExpressionKind getKind() {
        return ExpressionKind.TYPE;
    }

    @Override
    public TypeKind getTypeKind() {
        return TypeKind.DECLARED;
    }

    @Override
    public <P, R> R accept(final ExpressionVisitor<P, R> visitor, final P p) {
        return visitor.visitClassOrInterfaceTypeExpression(this, p);
    }
}
