package io.github.potjerodekool.codegen.loader.asm.visitor.signature;

import io.github.potjerodekool.codegen.loader.TypeElementLoader;
import io.github.potjerodekool.codegen.model.type.AbstractType;
import io.github.potjerodekool.codegen.model.type.TypeMirror;
import io.github.potjerodekool.codegen.model.type.TypeVariable;
import io.github.potjerodekool.codegen.model.type.TypeVariableImpl;

public class SuperClassOrInterfaceVisitor extends AbstractSignatureVisitor {

    private final AbstractSignatureVisitor parent;
    private final boolean isSuperClass;

    public SuperClassOrInterfaceVisitor(final int api,
                                        final TypeElementLoader loader,
                                        final AbstractSignatureVisitor parent,
                                        final boolean isSuperClass) {
        super(api, loader);
        this.parent = parent;
        this.isSuperClass = isSuperClass;
    }

    @Override
    public void visitClassType(final String name) {
        final var type = createClassType(name);
        push(type);

        if (isSuperClass) {
            parent.superClassEnd(type);
        } else {
            parent.interfaceEnd(type);
        }
    }

    @Override
    public void visitInnerClassType(final String name) {
        final var type = createClassType(name);
        final var enclosingType = pop();
        type.setEnclosingType(enclosingType);
        push(type);
    }

    @Override
    protected void typeVariable(final TypeVariable typeVariable) {
        addTypeArgument(typeVariable);
    }

    @Override
    protected void addTypeArgument(final TypeMirror type) {
        peek().addTypeArgument(type);
    }

    @Override
    protected void visitTypeArgument(final AbstractType type) {
        peek().addTypeArgument(type);
        push(type);
    }

    @Override
    protected void visitTypeArgumentEnd() {
        pop();
    }

    @Override
    public void visitEnd() {
    }
}
