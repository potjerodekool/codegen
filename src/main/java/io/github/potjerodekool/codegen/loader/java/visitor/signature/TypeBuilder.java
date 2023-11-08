package io.github.potjerodekool.codegen.loader.java.visitor.signature;

import io.github.potjerodekool.codegen.model.element.ElementKind;
import io.github.potjerodekool.codegen.model.element.Name;
import io.github.potjerodekool.codegen.model.symbol.ClassSymbol;
import io.github.potjerodekool.codegen.model.symbol.TypeVariableSymbol;
import io.github.potjerodekool.codegen.model.type.ClassType;
import io.github.potjerodekool.codegen.model.util.Elements;
import io.github.potjerodekool.codegen.model.util.type.Types;
import org.objectweb.asm.signature.SignatureVisitor;

public class TypeBuilder extends AbstractTypeBuilder {

    private final ClassType classType;
    private final ClassSymbol classSymbol;

    public TypeBuilder(final int api,
                       final Elements elements,
                       final Types types,
                       final ClassSymbol classSymbol) {
        super(api, elements, types, null);
        classType = new ClassType(classSymbol, true);
        classSymbol.setType(classType);
        this.classSymbol = classSymbol;
    }

    @Override
    public void visitFormalTypeParameter(final String name) {
        final var typeVariableSymbol = new TypeVariableSymbol(Name.of(name));
        classSymbol.addTypeParameter(typeVariableSymbol);
        final var formalType = typeVariableSymbol.asType();
        classType.addTypeArgument(formalType);
        setCurrentType(formalType);
    }

    @Override
    protected void childEnded() {
        setCurrentType(classType);
    }

    @Override
    public SignatureVisitor visitClassBound() {
        return new BoundTypeBuilder(api, elements, types, this);
    }

    @Override
    public SignatureVisitor visitInterfaceBound() {
        return new BoundTypeBuilder(api, elements, types, this);
    }

    @Override
    public void visitClassType(final String name) {
        final var clazz = loadClassType(name);

        if (clazz.asElement().getKind() == ElementKind.INTERFACE) {
            classSymbol.addInterface(clazz);
        }
    }

    @Override
    public ClassSymbol getClassSymbol() {
        return classSymbol;
    }
}
