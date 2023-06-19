package io.github.potjerodekool.codegen.loader.asm.visitor.signature;

import io.github.potjerodekool.codegen.loader.TypeElementLoader;
import io.github.potjerodekool.codegen.model.element.Name;
import io.github.potjerodekool.codegen.model.symbol.TypeVariableSymbol;
import io.github.potjerodekool.codegen.model.type.*;

public class TypeArgumentVisitor extends AbstractSignatureVisitor {

    private final AbstractSignatureVisitor parent;
    private final char wildcard;

    protected TypeArgumentVisitor(final int api,
                                  final TypeElementLoader loader,
                                  final AbstractSignatureVisitor parent,
                                  final char wildcard) {
        super(api, loader);
        this.parent = parent;
        this.wildcard = wildcard;
    }

    @Override
    public void visitClassType(final String name) {
        final var type = createClassType(name);
        parent.visitTypeArgument(type);
        push(type);
    }

    @Override
    public void visitTypeVariable(final String name) {
        if (wildcard == '=') {
            parent.typeVariable(createTypeVariable(name));
        } else {
            super.visitTypeVariable(name);
        }
    }

    private TypeVariable createTypeVariable(final String name) {
        return new TypeVariableSymbol(Name.of(name)).asType();
    }

    public void visitTypeArgument(final AbstractType type) {
        peek().addTypeArgument(type);
        push(type);
    }

    public void visitTypeArgument() {
        final var type = WildcardType.create();
        parent.visitTypeArgument(type);
        push(type);
    }

    @Override
    protected void typeVariable(final TypeVariable typeVariable) {
        final var type = (AbstractType) peek();
        type.addTypeArgument(typeVariable);
    }

    @Override
    protected void visitTypeArgumentEnd() {
        pop();
    }

    @Override
    public void addChild(final AbstractType type) {
        parent.addTypeArgument(type);
    }

    @Override
    public void visitEnd() {
        parent.visitTypeArgumentEnd();
    }
}
