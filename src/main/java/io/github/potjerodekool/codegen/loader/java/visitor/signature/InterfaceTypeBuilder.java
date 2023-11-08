package io.github.potjerodekool.codegen.loader.java.visitor.signature;

import io.github.potjerodekool.codegen.model.util.Elements;
import io.github.potjerodekool.codegen.model.util.type.Types;

public class InterfaceTypeBuilder extends AbstractTypeBuilder {

    public InterfaceTypeBuilder(final int api,
                                final Elements elements,
                                final Types types,
                                final AbstractTypeBuilder parent) {
        super(api, elements, types, parent);
    }

    @Override
    public void visitClassType(final String name) {
        final var interfaceType = loadClassType(name);
        parent.getClassSymbol().addInterface(interfaceType);
        setCurrentType(interfaceType);
    }
}
