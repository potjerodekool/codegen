package io.github.potjerodekool.codegen.extension.buildin;

import io.github.potjerodekool.codegen.extension.DefaultValueResolver;
import io.github.potjerodekool.codegen.model.tree.expression.Expression;
import io.github.potjerodekool.codegen.model.type.TypeMirror;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

public class CombinedDefaultValueResolver implements DefaultValueResolver {

    private final List<DefaultValueResolver> resolvers;

    public CombinedDefaultValueResolver(final List<DefaultValueResolver> resolvers) {
        this.resolvers = resolvers;
    }

    @Override
    public @Nullable Expression createDefaultValue(final TypeMirror typeMirror) {
        Expression expression = null;
        final var iterator = resolvers.iterator();

        while (expression == null && iterator.hasNext()) {
            final var resolver = iterator.next();
            expression = resolver.createDefaultValue(typeMirror);
        }

        return expression;
    }
}
