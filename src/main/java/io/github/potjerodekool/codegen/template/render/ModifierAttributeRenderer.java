package io.github.potjerodekool.codegen.template.render;

import io.github.potjerodekool.codegen.model.element.Modifier;
import org.stringtemplate.v4.AttributeRenderer;

import java.util.Locale;

public class ModifierAttributeRenderer implements AttributeRenderer<Modifier> {
    @Override
    public String toString(final Modifier modifier, final String formatString, final Locale locale) {
        return modifier.name().toLowerCase();
    }
}
