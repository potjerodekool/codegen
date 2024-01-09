package io.github.potjerodekool.codegen.template;

import io.github.potjerodekool.codegen.Language;
import io.github.potjerodekool.codegen.model.element.Modifier;
import io.github.potjerodekool.codegen.model.tree.expression.Operator;
import io.github.potjerodekool.codegen.template.adapter.ElementModelAdapter;
import io.github.potjerodekool.codegen.template.adapter.EnumModelAdapter;
import io.github.potjerodekool.codegen.template.adapter.SetModelAdapter;
import io.github.potjerodekool.codegen.template.model.TCompilationUnit;
import io.github.potjerodekool.codegen.template.model.element.Elem;
import io.github.potjerodekool.codegen.template.render.ModifierAttributeRenderer;
import io.github.potjerodekool.codegen.template.render.OperatorAttributeRenderer;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupDir;

import java.util.Set;

public class TemplateBasedGenerator {

    private final STGroup group;

    public TemplateBasedGenerator() {
        this.group = new STGroupDir("templates");
        group.registerModelAdaptor(Elem.class, new ElementModelAdapter());
        group.registerModelAdaptor(Enum.class, new EnumModelAdapter());
        group.registerModelAdaptor(Set.class, new SetModelAdapter());
        group.registerRenderer(Modifier.class, new ModifierAttributeRenderer());
        group.registerRenderer(Operator.class, new OperatorAttributeRenderer());
    }

    public STGroup getSTGroup() {
        return group;
    }

    public String doGenerate(final io.github.potjerodekool.codegen.model.CompilationUnit compilationUnit) {
        new io.github.potjerodekool.codegen.resolve.ImportOrganiser()
                .organiseImports(compilationUnit);

        final var transformer = new AstToTemplateModelTransformer();
        final var unit = transformer.transform(compilationUnit);

        new ImportOrganiser().organiseImports(unit);

        return doGenerate(unit);
    }


    public String doGenerate(final TCompilationUnit cu) {
        final ST template;

        if (cu.getLanguage() == Language.JAVA) {
            template = getSTGroup().getInstanceOf("java/compilationUnit");
        } else if (cu.getLanguage() == Language.KOTLIN) {
            template = getSTGroup().getInstanceOf("kotlin/compilationUnit");
        } else {
            throw new UnsupportedOperationException("Unsupported language: " + cu.getLanguage());
        }

        template.add("compilationUnit", cu);

        return template.render();
    }
}
