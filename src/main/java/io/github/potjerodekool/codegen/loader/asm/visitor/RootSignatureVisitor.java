package io.github.potjerodekool.codegen.loader.asm.visitor;

import io.github.potjerodekool.codegen.loader.TypeElementLoader;
import io.github.potjerodekool.codegen.loader.asm.visitor.signature.AbstractSignatureVisitor;
import io.github.potjerodekool.codegen.model.element.Name;
import io.github.potjerodekool.codegen.model.symbol.ClassSymbol;
import io.github.potjerodekool.codegen.model.symbol.TypeVariableSymbol;
import io.github.potjerodekool.codegen.model.type.AbstractType;
import io.github.potjerodekool.codegen.model.type.ClassType;
import io.github.potjerodekool.codegen.model.type.TypeVariableImpl;

public class RootSignatureVisitor extends AbstractSignatureVisitor {

    private final ClassSymbol classSymbol;
    private final ClassType classType;
    private TypeVariableSymbol typeVariableSymbol;
    private AbstractType currentType;
    public RootSignatureVisitor(final int api,
                                final TypeElementLoader loader,
                                final ClassSymbol classSymbol) {
        super(api, loader);
        this.classSymbol = classSymbol;
        this.classType = new ClassType(classSymbol, false);
        classSymbol.setType(classType);
    }

    @Override
    public void visitFormalTypeParameter(final String name) {
        this.typeVariableSymbol = new TypeVariableSymbol(Name.of(name));
        classSymbol.addTypeParameter(typeVariableSymbol);
        classType.addTypeArgument(typeVariableSymbol.asType());
        this.currentType = typeVariableSymbol.asType();
    }

    @Override
    protected void classBoundEnd(final AbstractType type) {
        if (currentType instanceof TypeVariableImpl typeVariable) {
            typeVariable.setUpperBound(type);
        }
    }

    @Override
    protected void interfacesBoundEnd(final AbstractType type) {
        if (currentType instanceof TypeVariableImpl typeVariable) {
            typeVariable.setUpperBound(type);
        }
    }

    @Override
    protected void superClassEnd(final AbstractType type) {
        classSymbol.setSuperType(type);
    }

    @Override
    protected void interfaceEnd(final AbstractType type) {
        classSymbol.addInterface(type);
    }

    @Override
    public void visitEnd() {
        if (typeVariableSymbol != null) {
            typeVariableSymbol = null;
        }
    }
}
