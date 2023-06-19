package io.github.potjerodekool.codegen.model.tree;

import io.github.potjerodekool.codegen.model.tree.expression.AbstractExpression;
import io.github.potjerodekool.codegen.model.tree.expression.Expression;
import io.github.potjerodekool.codegen.model.tree.expression.NameExpression;
import io.github.potjerodekool.codegen.model.tree.type.ParameterizedType;

import java.util.HashMap;
import java.util.Map;

public class AnnotationExpression extends AbstractExpression {

    private final ParameterizedType annotationType;

    private final Map<String, Expression> arguments = new HashMap<>();

    public AnnotationExpression(final String annotationType) {
        this(new ParameterizedType(new NameExpression(annotationType)));
    }

    public AnnotationExpression(final ParameterizedType annotationType) {
        this.annotationType = annotationType;
    }

    public AnnotationExpression(final String annotationType,
                                final Map<String, Expression> arguments) {
        this(new ParameterizedType(new NameExpression(annotationType)), arguments);
    }

    public AnnotationExpression(final ParameterizedType annotationType,
                                final Map<String, Expression> arguments) {
        this.annotationType = annotationType;
        this.arguments.putAll(arguments);
    }

    public ParameterizedType getAnnotationType() {
        return annotationType;
    }

    public Map<String, Expression> getArguments() {
        return arguments;
    }

    @Override
    public <R, P> R accept(final TreeVisitor<R, P> visitor, final P param) {
        return visitor.visitAnnotationExpression(this, param);
    }
}
