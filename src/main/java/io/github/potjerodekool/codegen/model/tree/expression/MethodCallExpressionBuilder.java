package io.github.potjerodekool.codegen.model.tree.expression;

import io.github.potjerodekool.codegen.model.tree.type.ClassOrInterfaceTypeExpression;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class MethodCallExpressionBuilder implements ExpressionBuilder<MethodCallExpression> {

    private final Expression target;
    private final String methodName;
    private final List<Expression> arguments = new ArrayList<>();

    public MethodCallExpressionBuilder(final Expression target,
                                       final String methodName) {
        this.target = target;
        this.methodName = methodName;
    }

    public static MethodCallExpressionBuilder invoke(final Expression target,
                                                     final String methodName) {
        return new MethodCallExpressionBuilder(target, methodName);
    }

    public static MethodCallExpressionBuilder invoke(final Expression target,
                                                     final String methodName,
                                                     final Expression... args) {
        return new MethodCallExpressionBuilder(target, methodName).withArgs(args);
    }

    public static MethodCallExpressionBuilder invoke(final Expression target,
                                                     final String methodName,
                                                     final List<Expression> args) {
        return new MethodCallExpressionBuilder(target, methodName).withArgs(args);
    }

    public static MethodCallExpressionBuilder invoke(final Expression target,
                                                     final String methodName,
                                                     final Supplier<List<Expression>> argSupplier) {
        return new MethodCallExpressionBuilder(target, methodName).withArgs(argSupplier.get());
    }

    public static MethodCallExpressionBuilder invoke(final String className,
                                                     final String methodName) {
        return new MethodCallExpressionBuilder(new ClassOrInterfaceTypeExpression(className), methodName);
    }

    public static MethodCallExpressionBuilder invoke(final String className,
                                                     final String methodName,
                                                     final Expression... args) {
        return new MethodCallExpressionBuilder(new ClassOrInterfaceTypeExpression(className), methodName)
                .withArgs(args);
    }

    public static MethodCallExpressionBuilder invoke(final String className,
                                                     final String methodName,
                                                     final List<Expression> args) {
        return new MethodCallExpressionBuilder(new ClassOrInterfaceTypeExpression(className), methodName)
                .withArgs(args);
    }

    public static MethodCallExpressionBuilder invoke(final String className,
                                                     final String methodName,
                                                     final Supplier<List<Expression>> argSupplier) {
        return new MethodCallExpressionBuilder(new ClassOrInterfaceTypeExpression(className), methodName)
                .withArgs(argSupplier.get());
    }

    public void addArgument(final Expression argument) {
        this.arguments.add(argument);
    }

    public void addArguments(final List<Expression> arguments) {
        this.arguments.addAll(arguments);
    }

    public MethodCallExpressionBuilder withArgs(final Supplier<List<Expression>> argSupplier) {
        addArguments(argSupplier.get());
        return this;
    }

    public MethodCallExpressionBuilder withArgs(final Expression... arguments) {
        addArguments(List.of(arguments));
        return this;
    }

    public MethodCallExpressionBuilder withArgs(final List<Expression> arguments) {
        addArguments(arguments);
        return this;
    }

    public MethodCallExpressionBuilder invoke(final String methodName) {
        return new MethodCallExpressionBuilder(build(), methodName);
    }

    @Override
    public MethodCallExpression build() {
        return new MethodCallExpression(
                target,
                methodName,
                arguments
        );
    }
}

