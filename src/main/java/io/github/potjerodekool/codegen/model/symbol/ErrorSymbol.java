package io.github.potjerodekool.codegen.model.symbol;

import io.github.potjerodekool.codegen.model.element.ElementKind;
import io.github.potjerodekool.codegen.model.element.Name;
import io.github.potjerodekool.codegen.model.element.NestingKind;
import io.github.potjerodekool.codegen.model.type.ErrorTypeImpl;

public class ErrorSymbol extends ClassSymbol {

    private ErrorSymbol() {
        super(ElementKind.OTHER, Name.of("error"), NestingKind.TOP_LEVEL, null);
    }

    public static ErrorSymbol create() {
        final var element = new ErrorSymbol();
        final var type = new ErrorTypeImpl(element);
        element.setType(type);
        return element;
    }

    @Override
    public ElementKind getKind() {
        return ElementKind.OTHER;
    }

}
