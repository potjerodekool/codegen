package io.github.potjerodekool.codegen.model.tree.expression;

import io.github.potjerodekool.codegen.model.tree.TreeVisitor;
import io.github.potjerodekool.codegen.model.type.DeclaredType;

public class NewClassExpression extends AbstractExpression {

    private final DeclaredType classType;

    public NewClassExpression(final DeclaredType classType) {
        this.classType = classType;
    }

    public DeclaredType getClassType() {
        return classType;
    }

    @Override
    public <R, P> R accept(final TreeVisitor<R, P> visitor, final P param) {
        return visitor.visitNewClassExpression(this, param);
    }
}
