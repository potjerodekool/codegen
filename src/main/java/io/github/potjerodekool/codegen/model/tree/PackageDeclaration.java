package io.github.potjerodekool.codegen.model.tree;

import io.github.potjerodekool.codegen.model.symbol.PackageSymbol;
import io.github.potjerodekool.codegen.model.tree.expression.IdentifierExpression;

public class PackageDeclaration extends AbstractTree {

    private final IdentifierExpression name;

    private PackageSymbol packageSymbol;

    public PackageDeclaration(final IdentifierExpression name) {
        this.name = name;
    }

    public IdentifierExpression getName() {
        return name;
    }

    public PackageSymbol getPackageSymbol() {
        return packageSymbol;
    }

    public void setPackageSymbol(final PackageSymbol packageSymbol) {
        this.packageSymbol = packageSymbol;
    }

    @Override
    public <R, P> R accept(final TreeVisitor<R, P> visitor, final P param) {
        return visitor.visitPackageDeclaration(this, param);
    }

    public boolean isDefaultPackage() {
        name.getName();

        return name.getName().contentEquals("");
    }
}
