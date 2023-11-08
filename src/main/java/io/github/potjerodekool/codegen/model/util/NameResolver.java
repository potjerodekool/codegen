package io.github.potjerodekool.codegen.model.util;

import io.github.potjerodekool.codegen.model.element.Element;
import io.github.potjerodekool.codegen.model.element.Name;

public interface NameResolver {

    Name resolveName(final Element element);

    Name resolveName(Name name);
}
