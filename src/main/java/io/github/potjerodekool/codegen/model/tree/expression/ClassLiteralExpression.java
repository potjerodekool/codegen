package io.github.potjerodekool.codegen.model.tree.expression;

import io.github.potjerodekool.codegen.model.tree.TreeVisitor;
import io.github.potjerodekool.codegen.model.tree.type.TypeExpression;

public class ClassLiteralExpression extends AbstractExpression implements LiteralExpression {

    private final TypeExpression clazz;

    public ClassLiteralExpression(final TypeExpression clazz) {
        this.clazz = clazz;
    }

    public TypeExpression getClazz() {
        return clazz;
    }

    @Override
    public <R, P> R accept(final TreeVisitor<R, P> visitor, final P param) {
        return visitor.visitLiteralExpression(this, param);
    }

    @Override
    public LiteralType getLiteralType() {
        return LiteralType.CLASS;
    }
}
