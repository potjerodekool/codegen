package io.github.potjerodekool.codegen.template;

import io.github.potjerodekool.codegen.template.model.TCompilationUnit;

public abstract class TemplateTest {

    protected String generateCode(final TCompilationUnit cu) {
        final var generator = new TemplateBasedGenerator();
        return fixLines(generator.doGenerate(cu));
    }

    protected String generateCode(final String templateName,
                                  final String objectName,
                                  final Object object) {
        final var generator = new TestGenerator();
        return generator.generate(templateName, objectName, object);
    }

    private String fixLines(final String s) {
        return s.replace("\r", "");
    }
}
