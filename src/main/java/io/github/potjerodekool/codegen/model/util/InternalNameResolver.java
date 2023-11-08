package io.github.potjerodekool.codegen.model.util;

import io.github.potjerodekool.codegen.model.element.Element;
import io.github.potjerodekool.codegen.model.element.Name;
import io.github.potjerodekool.codegen.model.element.QualifiedNameable;

public class InternalNameResolver implements NameResolver {
    @Override
    public Name resolveName(final Element element) {
        final Name className;

        if (element instanceof QualifiedNameable qualifiedNameable) {
            className = qualifiedNameable.getQualifiedName();
        } else {
            className = element.getSimpleName();
        }

        return resolveName(className);
    }

    @Override
    public Name resolveName(final Name name) {
        return Name.of(
                name.toString().replace('.', '/')
                        .replace('$', '.')
        );
    }
}
