package io.github.potjerodekool.codegen.model.tree;

import io.github.potjerodekool.codegen.model.symbol.PackageSymbol;
import io.github.potjerodekool.codegen.model.tree.expression.NameExpression;

public class PackageDeclaration extends AbstractTree {

    private final NameExpression name;

    private PackageSymbol packageSymbol;

    public PackageDeclaration(final NameExpression name) {
        this.name = name;
    }

    public NameExpression getName() {
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
}
