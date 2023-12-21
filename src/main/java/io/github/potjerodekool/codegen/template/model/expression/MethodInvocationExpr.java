package io.github.potjerodekool.codegen.template.model.expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MethodInvocationExpr implements Expr {

    private Expr target;

    private String name;

    private final List<Expr> arguments = new ArrayList<>();

    public MethodInvocationExpr() {
    }

    public MethodInvocationExpr(final Expr target,
                                final String name) {
        this.name = name;
        this.target = target;
    }

    public MethodInvocationExpr(final Expr target,
                                final String name,
                                final Expr argument) {
        this.name = name;
        this.target = target;
        this.arguments.add(argument);
    }

    public MethodInvocationExpr(final Expr target,
                                final String name,
                                final Expr... arguments) {
        this.name = name;
        this.target = target;
        this.arguments.addAll(Arrays.asList(arguments));
    }


    public Expr getTarget() {
        return target;
    }

    public MethodInvocationExpr target(final Expr target) {
        this.target = target;
        return this;
    }

    public String getName() {
        return name;
    }

    public MethodInvocationExpr name(final String name) {
        this.name = name;
        return this;
    }

    public List<Expr> getArguments() {
        return arguments;
    }

    public MethodInvocationExpr argument(final Expr argument) {
        arguments.add(argument);
        return this;
    }

    public MethodInvocationExpr arguments(final Expr... arguments) {
        this.arguments.addAll(Arrays.asList(arguments));
        return this;
    }

    public MethodInvocationExpr arguments(final List<Expr> arguments) {
        this.arguments.addAll(arguments);
        return this;
    }

    @Override
    public ExpressionKind getKind() {
        return ExpressionKind.METHOD_INVOCATION;
    }

    @Override
    public <P, R> R accept(final ExpressionVisitor<P, R> visitor, final P p) {
        return visitor.visitMethodInvocationExpression(this, p);
    }
}
