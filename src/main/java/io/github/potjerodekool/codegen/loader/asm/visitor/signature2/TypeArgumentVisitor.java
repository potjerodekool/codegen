package io.github.potjerodekool.codegen.loader.asm.visitor.signature2;

import io.github.potjerodekool.codegen.loader.TypeElementLoader;
import io.github.potjerodekool.codegen.model.type.ClassType;
import io.github.potjerodekool.codegen.model.type.TypeMirror;
import io.github.potjerodekool.codegen.model.util.type.Types;

public class TypeArgumentVisitor extends AbstractSignatureVisitor2 {

    private final char wildcard;

    public TypeArgumentVisitor(final int api,
                               final TypeElementLoader loader,
                               final Types types,
                               final AbstractSignatureVisitor2 parent,
                               final char wildcard) {
        super(api, loader, types, parent);
        this.wildcard = wildcard;
    }

    @Override
    protected void addType(final TypeMirror type) {
        parent.addTypeArgument(type);
    }

}
