package io.github.potjerodekool.codegen.model.tree.kotlin;

import io.github.potjerodekool.codegen.model.tree.AnnotationExpression;
import io.github.potjerodekool.codegen.model.tree.expression.Expression;
import io.github.potjerodekool.codegen.model.tree.type.ClassOrInterfaceTypeExpression;

import java.util.Map;

public class KAnnotationExpression extends AnnotationExpression {

    private final Target target;

    public KAnnotationExpression(final ClassOrInterfaceTypeExpression annotationType,
                                 final Map<String, Expression> arguments) {
        this(null, annotationType, arguments);
    }

    public KAnnotationExpression(final Target target,
                                 final ClassOrInterfaceTypeExpression annotationType,
                                 final Map<String, Expression> arguments) {
        super(annotationType, arguments);
        this.target = target;
    }

    public Target getTarget() {
        return target;
    }

    public enum Target {
        FIELD()
    }
}
