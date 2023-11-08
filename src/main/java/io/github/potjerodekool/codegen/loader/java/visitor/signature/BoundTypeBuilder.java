package io.github.potjerodekool.codegen.loader.java.visitor.signature;

import io.github.potjerodekool.codegen.model.element.Name;
import io.github.potjerodekool.codegen.model.symbol.TypeVariableSymbol;
import io.github.potjerodekool.codegen.model.type.ClassType;
import io.github.potjerodekool.codegen.model.type.TypeMirror;
import io.github.potjerodekool.codegen.model.type.TypeVariableImpl;
import io.github.potjerodekool.codegen.model.util.Elements;
import io.github.potjerodekool.codegen.model.util.type.Types;

public class BoundTypeBuilder extends AbstractTypeBuilder {

    public BoundTypeBuilder(final int api,
                            final Elements elements,
                            final Types types,
                            final AbstractTypeBuilder parent) {
        super(api, elements, types, parent);
    }

    @Override
    public void visitClassType(final String name) {
        final var classType = loadClassType(name);
        final var parentType = (TypeVariableImpl) parent.getCurrentType();
        parentType.setUpperBound(classType);
        setCurrentType(classType);
    }

    @Override
    public void visitTypeVariable(final String name) {
        final var type = new TypeVariableSymbol(Name.of(name)).asType();
        final var currentType = getCurrentType();


        if (currentType instanceof ClassType currentClassType) {
            //TODO
            currentClassType.addTypeArgument(type);
            throw new UnsupportedOperationException();
        } else if (currentType instanceof TypeVariableImpl currentTypeVar) {
            currentTypeVar.setUpperBound(type);
        }

        setCurrentType(type);
    }

    @Override
    public TypeMirror getCurrentType() {
        final var currentType = super.getCurrentType();

        if (currentType != null) {
            return currentType;
        } else {
            return parent.getCurrentType();
        }
    }

    @Override
    public void visitEnd() {
        parent.childEnded();
        super.visitEnd();
    }
}
