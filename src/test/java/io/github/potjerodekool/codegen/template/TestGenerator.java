package io.github.potjerodekool.codegen.template;

public class TestGenerator extends TemplateBasedGenerator {

    public String generate(final String templateName,
                           final String objectName,
                           final Object object) {
        final var template = getSTGroup().getInstanceOf(templateName);
        template.add(objectName, object);
        return template.render();
    }
}
