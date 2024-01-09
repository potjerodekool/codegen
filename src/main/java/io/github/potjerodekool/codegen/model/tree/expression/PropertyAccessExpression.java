package io.github.potjerodekool.codegen.model.tree.expression;

import io.github.potjerodekool.codegen.model.tree.TreeVisitor;
import io.github.potjerodekool.codegen.model.type.TypeMirror;

public class PropertyAccessExpression extends AbstractExpression {

    private Expression target;

    private IdentifierExpression name;

    private TypeMirror type;

    public Expression getTarget() {
        return target;
    }

    public void setTarget(final Expression target) {
        this.target = target;
    }

    public IdentifierExpression getName() {
        return name;
    }

    public void setName(final IdentifierExpression name) {
        this.name = name;
    }

    @Override
    public TypeMirror getType() {
        return type;
    }

    @Override
    public PropertyAccessExpression type(final TypeMirror type) {
        this.type = type;
        return this;
    }

    @Override
    public <R, P> R accept(final TreeVisitor<R, P> visitor, final P param) {
        return visitor.visitPropertyAccessExpression(this, param);
    }
}
