package io.github.potjerodekool.codegen.model.tree.expression;

import io.github.potjerodekool.codegen.model.tree.TreeVisitor;

import java.util.*;

public class MethodCallExpression extends AbstractExpression {

    private Expression target;

    private IdentifierExpression methodName;

    private final List<Expression> arguments = new ArrayList<>();

    public MethodCallExpression() {
    }

    public MethodCallExpression(final Expression target,
                                final String methodName) {
        this(target, methodName, List.of());
    }

    public MethodCallExpression(final Expression target,
                                final String methodName,
                                final List<? extends Expression> arguments) {
        this.target = target;
        this.methodName = new IdentifierExpression(methodName);
        this.arguments.addAll(arguments);
    }

    public MethodCallExpression(final Expression target,
                                final String methodName,
                                final Expression... arguments) {
        this(target, methodName, List.of(arguments));
    }

    public MethodCallExpression(final Expression target,
                                final String methodName,
                                final Expression argument) {
        this(target, methodName, List.of(argument));
    }

    public Optional<Expression> getTarget() {
        return Optional.ofNullable(target);
    }

    public MethodCallExpression target(final Expression target) {
        this.target = target;
        return this;
    }

    public IdentifierExpression getMethodName() {
        return methodName;
    }

    public MethodCallExpression methodName(final String methodName) {
        this.methodName = new IdentifierExpression(methodName);
        return this;
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    public MethodCallExpression argument(final Expression argument) {
        this.arguments.add(argument);
        return this;
    }

    public MethodCallExpression arguments(final Expression... arguments) {
        Collections.addAll(this.arguments, arguments);
        return this;
    }

    public MethodCallExpression arguments(final List<? extends Expression> arguments) {
        this.arguments.addAll(arguments);
        return this;
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
