package io.github.potjerodekool.codegen.loader.asm.visitor.signature2;

import io.github.potjerodekool.codegen.loader.TypeElementLoader;
import io.github.potjerodekool.codegen.model.type.ClassType;
import io.github.potjerodekool.codegen.model.type.WildcardType;
import io.github.potjerodekool.codegen.model.util.type.Types;

public class ArrayTypeVisitor extends AbstractSignatureVisitor2 {

    private ClassType componentType;

    public ArrayTypeVisitor(final int api,
                            final TypeElementLoader loader,
                            final Types types,
                            final AbstractSignatureVisitor2 parent) {
        super(api, loader, types, parent);
    }

    @Override
    public void visitClassType(final String name) {
        componentType = createClassType(name);
    }

    @Override
    public void visitTypeArgument() {
        final var type = WildcardType.create();
        componentType.addTypeArgument(type);
    }

    @Override
    public void visitEnd() {
        parent.addType(types.getArrayType(componentType));
    }
}
