package io.github.potjerodekool.codegen.loader.asm.visitor.signature;

import io.github.potjerodekool.codegen.loader.TypeElementLoader;
import io.github.potjerodekool.codegen.model.type.ClassType;
import io.github.potjerodekool.codegen.model.type.TypeVariable;
import io.github.potjerodekool.codegen.model.type.TypeVariableImpl;

public class ClassOrInterfaceBoundVisitor extends AbstractSignatureVisitor {

    private final AbstractSignatureVisitor parent;
    private final boolean isClassBound;

    public ClassOrInterfaceBoundVisitor(final int api,
                                        final TypeElementLoader loader,
                                        final AbstractSignatureVisitor parent,
                                        final boolean isClassBound) {
        super(api, loader);
        this.parent = parent;
        this.isClassBound = isClassBound;
    }

    @Override
    public void visitClassType(final String name) {
        this.push(createClassType(name));
    }

    @Override
    protected void typeVariable(final TypeVariable typeVariable) {
        peek().addTypeArgument(typeVariable);
    }

    @Override
    public void visitInnerClassType(final String name) {
        final var outerClass = (ClassType) this.peek();
        final var outerName = outerClass.asElement().getQualifiedName().toString().replace('.', '/');
        final var innerName = outerName + "$" + name;
        final var innerClass = createClassType(innerName);
        innerClass.setEnclosingType(outerClass);
        push(innerClass);
    }

    @Override
    public void visitEnd() {
        if (isClassBound) {
            parent.classBoundEnd(peek());
        } else {
            parent.interfacesBoundEnd(peek());
        }
    }
}
