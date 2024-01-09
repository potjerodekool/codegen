package io.github.potjerodekool.codegen.template.model.expression;

import java.util.ArrayList;
import java.util.List;

public class NewClassExpr implements Expr {

    private String name;
    private final List<Expr> arguments = new ArrayList<>();
    @Override
    public ExpressionKind getKind() {
        return ExpressionKind.NEW_CLASS;
    }

    @Override
    public <P, R> R accept(final ExpressionVisitor<P, R> visitor, final P p) {
        return visitor.visitNewClassExpression(this, p);
    }

    public String getName() {
        return name;
    }

    public NewClassExpr name(final String className) {
        this.name = className;
        return this;
    }

    public List<Expr> getArguments() {
        return arguments;
    }

    public NewClassExpr arguments(final List<Expr> arguments) {
        this.arguments.addAll(arguments);
        return this;
    }
}
