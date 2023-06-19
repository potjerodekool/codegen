package io.github.potjerodekool.codegen.loader.asm.visitor.signature2;

import io.github.potjerodekool.codegen.loader.TypeElementLoader;
import io.github.potjerodekool.codegen.model.element.Name;
import io.github.potjerodekool.codegen.model.symbol.ClassSymbol;
import io.github.potjerodekool.codegen.model.symbol.TypeVariableSymbol;
import io.github.potjerodekool.codegen.model.type.AbstractType;
import io.github.potjerodekool.codegen.model.type.ClassType;
import io.github.potjerodekool.codegen.model.util.type.Types;

public class RootSignatureVisitor2 extends AbstractSignatureVisitor2 {

    private final ClassSymbol classSymbol;
    private final ClassType classType;
    private TypeVariableSymbol typeVariableSymbol;
    private AbstractType currentType;

    public RootSignatureVisitor2(final int api,
                                 final TypeElementLoader loader,
                                 final Types types,
                                 final ClassSymbol classSymbol) {
        super(api, loader, types, null);
        this.classSymbol = classSymbol;
        this.classType = new ClassType(classSymbol, false);
    }

    @Override
    protected void superClassEnd(final ClassType classType) {
        classSymbol.setSuperType(classType);
    }

    @Override
    protected void interfaceEnd(final ClassType classType) {
        classSymbol.addInterface(classType);
    }

    @Override
    public void visitFormalTypeParameter(final String name) {
        this.typeVariableSymbol = new TypeVariableSymbol(Name.of(name));
        classSymbol.addTypeParameter(typeVariableSymbol);
        classType.addTypeArgument(typeVariableSymbol.asType());
        this.currentType = typeVariableSymbol.asType();
    }

    public ClassSymbol getClassSymbol() {
        return classSymbol;
    }

}
