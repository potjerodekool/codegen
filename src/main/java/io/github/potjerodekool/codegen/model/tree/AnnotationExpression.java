package io.github.potjerodekool.codegen.model.tree;

import io.github.potjerodekool.codegen.model.tree.expression.AbstractExpression;
import io.github.potjerodekool.codegen.model.tree.expression.Expression;
import io.github.potjerodekool.codegen.model.tree.type.ClassOrInterfaceTypeExpression;

import java.util.HashMap;
import java.util.Map;

public class AnnotationExpression extends AbstractExpression {

    private ClassOrInterfaceTypeExpression annotationType;

    private final Map<String, Expression> arguments = new HashMap<>();

    public AnnotationExpression() {
    }

    public AnnotationExpression(final String annotationType,
                                final Expression value) {
        this(new ClassOrInterfaceTypeExpression(annotationType), Map.of("value", value));
    }

    public AnnotationExpression(final String annotationType) {
        this(new ClassOrInterfaceTypeExpression(annotationType));
    }

    public AnnotationExpression(final ClassOrInterfaceTypeExpression annotationType) {
        this.annotationType = annotationType;
    }

    public AnnotationExpression(final ClassOrInterfaceTypeExpression annotationType,
                                final Expression value) {
        this(annotationType, Map.of("value", value));
    }

    public AnnotationExpression(final String annotationType,
                                final Map<String, Expression> arguments) {
        this(new ClassOrInterfaceTypeExpression(annotationType), arguments);
    }

    public AnnotationExpression(final ClassOrInterfaceTypeExpression annotationType,
                                final Map<String, Expression> arguments) {
        this.annotationType = annotationType;
        this.arguments.putAll(arguments);
    }

    public ClassOrInterfaceTypeExpression getAnnotationType() {
        return annotationType;
    }

    public AnnotationExpression annotationType(final ClassOrInterfaceTypeExpression annotationType) {
        this.annotationType = annotationType;
        return this;
    }

    public Map<String, Expression> getArguments() {
        return arguments;
    }

    public AnnotationExpression argument(final String name,
                                         final Expression expression) {
        arguments.put(name, expression);
        return this;
    }

    @Override
    public <R, P> R accept(final TreeVisitor<R, P> visitor, final P param) {
        return visitor.visitAnnotationExpression(this, param);
    }
}
