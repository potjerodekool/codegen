package io.github.potjerodekool.codegen.loader.asm.visitor.signature2;

import io.github.potjerodekool.codegen.loader.TypeElementLoader;
import io.github.potjerodekool.codegen.model.type.ClassType;
import io.github.potjerodekool.codegen.model.type.TypeMirror;
import io.github.potjerodekool.codegen.model.util.type.Types;

public class SuperClassOrInterfaceVisitor extends AbstractSignatureVisitor2 {

    private final boolean isSuperClass;
    private ClassType classType;

    public SuperClassOrInterfaceVisitor(final int api,
                                        final TypeElementLoader loader,
                                        final Types types,
                                        final boolean isSuperClass,
                                        final AbstractSignatureVisitor2 parent) {
        super(api, loader, types, parent);
        this.isSuperClass = isSuperClass;
    }

    @Override
    public void visitClassType(final String name) {
        classType = createClassType(name);
    }

    @Override
    protected void addTypeArgument(final TypeMirror type) {
        classType.addTypeArgument(type);
    }

    @Override
    public void visitEnd() {
        if (isSuperClass) {
            parent.superClassEnd(classType);
        } else {
            parent.interfaceEnd(classType);
        }
    }
}
