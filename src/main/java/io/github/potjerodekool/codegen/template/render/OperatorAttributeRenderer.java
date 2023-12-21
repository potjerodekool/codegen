package io.github.potjerodekool.codegen.template.render;

import io.github.potjerodekool.codegen.model.tree.expression.Operator;
import org.stringtemplate.v4.AttributeRenderer;

import java.util.Locale;

public class OperatorAttributeRenderer implements AttributeRenderer<Operator> {
    @Override
    public String toString(final Operator operator, final String formatString, final Locale locale) {
        return operator.getValue();
    }
}
