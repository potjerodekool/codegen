package io.github.potjerodekool.codegen.model.tree.expression;

import io.github.potjerodekool.codegen.model.element.Name;
import io.github.potjerodekool.codegen.model.tree.TreeVisitor;
import io.github.potjerodekool.codegen.model.tree.type.ClassOrInterfaceTypeExpression;

public class FieldAccessExpression extends AbstractExpression {

    private final Expression scope;

    private final Name field;

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

    public Name getField() {
        return field;
    }

    @Override
    public <R, P> R accept(final TreeVisitor<R, P> visitor,
                           final P param) {
        return visitor.visitFieldAccessExpression(this, param);
    }
}
