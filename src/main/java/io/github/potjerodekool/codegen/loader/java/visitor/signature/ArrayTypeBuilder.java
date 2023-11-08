package io.github.potjerodekool.codegen.loader.java.visitor.signature;

import io.github.potjerodekool.codegen.model.type.ClassType;
import io.github.potjerodekool.codegen.model.type.TypeMirror;
import io.github.potjerodekool.codegen.model.type.immutable.WildcardType;
import io.github.potjerodekool.codegen.model.util.Elements;
import io.github.potjerodekool.codegen.model.util.type.Types;

public class ArrayTypeBuilder extends AbstractTypeBuilder {

    private TypeMirror type;

    public ArrayTypeBuilder(final int api,
                            final Elements elements,
                            final Types types, final AbstractTypeBuilder parent) {
        super(api, elements, types, parent);
    }

    @Override
    public void visitClassType(final String name) {
        final var classType = loadClassType(name);
        this.type = classType;
        setCurrentType(classType);
    }

    @Override
    public void visitTypeArgument() {
        final var wildCardType = WildcardType.create();
        final var classType = (ClassType) type;
        classType.addTypeArgument(wildCardType);
        setCurrentType(wildCardType);
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
        parent.addChildType(types.getArrayType(type));
    }
}
