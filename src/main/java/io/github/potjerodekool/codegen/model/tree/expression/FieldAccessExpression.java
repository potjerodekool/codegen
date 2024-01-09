package io.github.potjerodekool.codegen.model.tree.expression;

import io.github.potjerodekool.codegen.model.element.Name;
import io.github.potjerodekool.codegen.model.tree.TreeVisitor;
import io.github.potjerodekool.codegen.model.tree.type.ClassOrInterfaceTypeExpression;

public class FieldAccessExpression extends AbstractExpression {

    private Expression scope;

    private Name field;

    public FieldAccessExpression() {
    }

    public FieldAccessExpression(final Expression scope,
                                 final Name field) {
        this.scope = scope;
        this.field = field;
    }

    public FieldAccessExpression(final Expression scope,
                                 final String field) {
        this(scope, Name.of(field));
    }

    public FieldAccessExpression(final String className,
                                 final String field) {
        this(new ClassOrInterfaceTypeExpression(className), Name.of(field));
    }

    public Expression getScope() {
        return scope;
    }

    public FieldAccessExpression scope(final Expression scope) {
        this.scope = scope;
        return this;
    }

    public Name getField() {
        return field;
    }

    public FieldAccessExpression field(final Name field) {
        this.field = field;
        return this;
    }

    @Override
    public <R, P> R accept(final TreeVisitor<R, P> visitor,
                           final P param) {
        return visitor.visitFieldAccessExpression(this, param);
    }
}
