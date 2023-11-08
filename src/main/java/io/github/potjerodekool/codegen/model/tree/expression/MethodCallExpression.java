package io.github.potjerodekool.codegen.model.tree.expression;

import io.github.potjerodekool.codegen.model.tree.TreeVisitor;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MethodCallExpression extends AbstractExpression {

    private final @Nullable Expression target;

    private final String methodName;

    private final List<Expression> arguments = new ArrayList<>();

    public MethodCallExpression(final @Nullable Expression target,
                                final String methodName) {
        this(target, methodName, List.of());
    }

    public MethodCallExpression(final @Nullable Expression target,
                                final String methodName,
                                final List<? extends Expression> arguments) {
        this.target = target;
        this.methodName = methodName;
        this.arguments.addAll(arguments);
    }

    public MethodCallExpression(final @Nullable Expression target,
                                final String methodName,
                                final Expression... arguments) {
        this(target, methodName, List.of(arguments));
    }

    public MethodCallExpression(final @Nullable Expression target,
                                final String methodName,
                                final Expression argument) {
        this(target, methodName, List.of(argument));
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

    public MethodCallExpression invoke(final String methodName) {
        return new MethodCallExpression(
                this,
                methodName
        );
    }

    public MethodCallExpression invoke(final String methodName,
                                       final Expression argument) {
        return new MethodCallExpression(
                this,
                methodName,
                List.of(argument)
        );
    }

    public MethodCallExpression invoke(final String methodName,
                                       final Expression... arguments) {
        return new MethodCallExpression(
                this,
                methodName,
                List.of(arguments)
        );
    }

    public MethodCallExpression invoke(final String methodName,
                                       final List<Expression> arguments) {
        return new MethodCallExpression(
                this,
                methodName,
                arguments
        );
    }

    @Override
    public <R, P> R accept(final TreeVisitor<R, P> visitor, final P param) {
        return visitor.visitMethodCall(this, param);
    }
}
