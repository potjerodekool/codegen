package io.github.potjerodekool.codegen.loader.java.visitor.signature;

import io.github.potjerodekool.codegen.model.element.Name;
import io.github.potjerodekool.codegen.model.symbol.TypeVariableSymbol;
import io.github.potjerodekool.codegen.model.type.ClassType;
import io.github.potjerodekool.codegen.model.type.TypeMirror;
import io.github.potjerodekool.codegen.model.type.immutable.WildcardType;
import io.github.potjerodekool.codegen.model.util.Elements;
import io.github.potjerodekool.codegen.model.util.type.Types;

public class TypeArgumentBuilder extends AbstractTypeBuilder {

    public TypeArgumentBuilder(final int api,
                               final Elements elements,
                               final Types types,
                               final AbstractTypeBuilder parent, final char wildcard) {
        super(api, elements, types, parent);
    }

    @Override
    public void visitClassType(final String name) {
        final var type = loadClassType(name);
        final var parentType = (ClassType) parent.getCurrentType();
        parentType.addTypeArgument(type);
        setCurrentType(type);
    }

    @Override
    public void visitTypeVariable(final String name) {
        final var type = new TypeVariableSymbol(Name.of(name)).asType();
        final var currentType = (ClassType) getCurrentType();
        currentType.addTypeArgument(type);
        setCurrentType(type);
    }

    @Override
    public void visitTypeArgument() {
        final var wildCardType = WildcardType.create();
        final var currentType = (ClassType) getCurrentType();
        currentType.addTypeArgument(wildCardType);
    }

    @Override
    public TypeMirror getCurrentType() {
        var currentType = super.getCurrentType();
        if (currentType != null) {
            return currentType;
        }
        return parent.getCurrentType();
    }

    @Override
    protected void addChildType(final TypeMirror childType) {
        final var currentType = (ClassType) getCurrentType();
        currentType.addTypeArgument(childType);
    }
}
