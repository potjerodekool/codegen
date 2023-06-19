package io.github.potjerodekool.codegen.model.tree.expression;

import io.github.potjerodekool.codegen.model.tree.TreeVisitor;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MethodCallExpression extends AbstractExpression {

    private final @Nullable Expression target;

    private final String methodName;

    private final List<Expression> arguments;

    public MethodCallExpression(final @Nullable Expression target,
                                final String methodName) {
        this(target, methodName, new ArrayList<>());
    }

    public MethodCallExpression(final @Nullable Expression target,
                                final String methodName,
                                final List<Expression> arguments) {
        this.target = target;
        this.methodName = methodName;
        this.arguments = arguments;
    }

    public Optional<Expression> getTarget() {
        return Optional.ofNullable(target);
    }

    public String getMethodName() {
        return methodName;
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    @Override
    public <R, P> R accept(final TreeVisitor<R, P> visitor, final P param) {
        return visitor.visitMethodCall(this, param);
    }
}
