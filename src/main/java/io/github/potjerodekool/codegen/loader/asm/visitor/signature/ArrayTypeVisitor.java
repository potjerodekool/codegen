package io.github.potjerodekool.codegen.loader.asm.visitor.signature;

import io.github.potjerodekool.codegen.loader.TypeElementLoader;
import io.github.potjerodekool.codegen.model.type.AbstractType;
import io.github.potjerodekool.codegen.model.type.WildcardType;
import io.github.potjerodekool.codegen.model.type.java.JavaArrayTypeImpl;

// Ljava/lang/Object;Ljava/security/PrivilegedAction<[Ljava/lang/Class<*>;>;
public class ArrayTypeVisitor extends AbstractSignatureVisitor {

    private final AbstractSignatureVisitor parent;
    protected ArrayTypeVisitor(final int api,
                               final TypeElementLoader loader,
                               final AbstractSignatureVisitor parent) {
        super(api, loader);
        this.parent = parent;
    }

    @Override
    public void visitClassType(final String name) {
        push(createClassType(name));
    }
    @Override
    public void visitTypeArgument() {
        final var type = WildcardType.create();
        parent.visitTypeArgument(type);
        push(type);
    }
    @Override
    public void visitEnd() {
        final var componentType = pop();
        parent.addChild(new JavaArrayTypeImpl(componentType, true));
    }
}
