package io.github.potjerodekool.codegen.model.tree.expression;

import io.github.potjerodekool.codegen.model.tree.TreeVisitor;
import io.github.potjerodekool.codegen.model.tree.type.ClassOrInterfaceTypeExpression;

import java.util.ArrayList;
import java.util.List;

public class NewClassExpression extends AbstractExpression {

    private final ClassOrInterfaceTypeExpression clazz;
    private final List<Expression> arguments = new ArrayList<>();

    public NewClassExpression(final ClassOrInterfaceTypeExpression clazz) {
        this(clazz, List.of());
    }

    public NewClassExpression(final ClassOrInterfaceTypeExpression clazz,
                              final List<Expression> arguments) {
        this.clazz = clazz;
        this.arguments.addAll(arguments);
    }

    public NewClassExpression(final String clazz) {
        this(new ClassOrInterfaceTypeExpression(clazz));
    }

    public NewClassExpression(final String clazz,
                              final List<Expression> arguments) {
        this(new ClassOrInterfaceTypeExpression(clazz), arguments);
    }

    public ClassOrInterfaceTypeExpression getClazz() {
        return clazz;
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    @Override
    public <R, P> R accept(final TreeVisitor<R, P> visitor, final P param) {
        return visitor.visitNewClassExpression(this, param);
    }
}
