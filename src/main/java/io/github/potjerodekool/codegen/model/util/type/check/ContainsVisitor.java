package io.github.potjerodekool.codegen.model.util.type.check;

import io.github.potjerodekool.codegen.model.element.java.ElementFilter;
import io.github.potjerodekool.codegen.model.symbol.ClassSymbol;
import io.github.potjerodekool.codegen.model.type.DeclaredType;
import io.github.potjerodekool.codegen.model.type.TypeMirror;
import io.github.potjerodekool.codegen.model.util.Elements;

public class ContainsVisitor extends AbstractTypeRelation {

    @Override
    public Boolean visitDeclared(final DeclaredType declaredType,
                                 final TypeMirror type) {
        if (type == declaredType || !(type instanceof DeclaredType)) {
            return false;
        }

        final var otherElement = ((DeclaredType) type).asElement();
        final var otherElementName = Elements.getQualifiedName(otherElement);
        final var element = (ClassSymbol) declaredType.asElement();

        return ElementFilter.types(element)
                .anyMatch(it -> it.getQualifiedName().contentEquals(otherElementName));
    }
}
