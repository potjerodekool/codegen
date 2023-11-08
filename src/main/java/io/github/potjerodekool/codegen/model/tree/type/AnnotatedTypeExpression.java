package io.github.potjerodekool.codegen.model.tree.type;

import io.github.potjerodekool.codegen.model.tree.AnnotationExpression;
import io.github.potjerodekool.codegen.model.tree.TreeVisitor;
import io.github.potjerodekool.codegen.model.tree.expression.AbstractExpression;
import io.github.potjerodekool.codegen.model.tree.expression.Expression;
import io.github.potjerodekool.codegen.model.type.TypeKind;

import java.util.ArrayList;
import java.util.List;

public class AnnotatedTypeExpression extends AbstractExpression implements TypeExpression {

    private final Expression identifier;

    private final List<AnnotationExpression> annotations;

    private final boolean isNullable;

    public AnnotatedTypeExpression(final Expression identifier) {
        this(identifier, new ArrayList<>());
    }


    public AnnotatedTypeExpression(final Expression identifier,
                                   final List<AnnotationExpression> annotations) {
        this(identifier, annotations, false);
    }

    public AnnotatedTypeExpression(final Expression identifier,
                                   final List<AnnotationExpression> annotations,
                                   final boolean isNullable) {
        this.identifier = identifier;
        this.annotations = annotations;
        this.isNullable = isNullable;
    }

    public Expression getIdentifier() {
        return identifier;
    }

    public List<AnnotationExpression> getAnnotations() {
        return annotations;
    }

    @Override
    public boolean isNullable() {
        return isNullable;
    }

    @Override
    public TypeKind getKind() {
         if (identifier instanceof TypeExpression typeExpression) {
             return typeExpression.getKind();
         } else {
             return null;
         }
    }

    @Override
    public <R, P> R accept(final TreeVisitor<R, P> visitor, final P param) {
        return visitor.visitAnnotatedType(this, param);
    }
}
