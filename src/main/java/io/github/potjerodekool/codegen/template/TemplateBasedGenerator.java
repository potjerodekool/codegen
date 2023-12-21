package io.github.potjerodekool.codegen.template;

import io.github.potjerodekool.codegen.Environment;
import io.github.potjerodekool.codegen.Language;
import io.github.potjerodekool.codegen.io.Location;
import io.github.potjerodekool.codegen.kotlin.JavaToKotlinConverter;
import io.github.potjerodekool.codegen.model.element.Modifier;
import io.github.potjerodekool.codegen.model.tree.expression.Operator;
import io.github.potjerodekool.codegen.resolve.Enter;
import io.github.potjerodekool.codegen.resolve.Resolver;
import io.github.potjerodekool.codegen.template.adapter.ElementModelAdapter;
import io.github.potjerodekool.codegen.template.adapter.EnumModelAdapter;
import io.github.potjerodekool.codegen.template.adapter.SetModelAdapter;
import io.github.potjerodekool.codegen.template.model.CompilationUnit;
import io.github.potjerodekool.codegen.template.model.element.Elem;
import io.github.potjerodekool.codegen.template.render.ModifierAttributeRenderer;
import io.github.potjerodekool.codegen.template.render.OperatorAttributeRenderer;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupDir;

import java.util.Set;

public abstract class TemplateBasedGenerator {

    private final Environment environment;

    private final STGroup group;
    public TemplateBasedGenerator(final Environment environment) {
        this.environment = environment;
        this.group = new STGroupDir("templates");
        group.registerModelAdaptor(Elem.class, new ElementModelAdapter());
        group.registerModelAdaptor(Enum.class, new EnumModelAdapter());
        group.registerModelAdaptor(Set.class, new SetModelAdapter());
        group.registerRenderer(Modifier.class, new ModifierAttributeRenderer());
        group.registerRenderer(Operator.class, new OperatorAttributeRenderer());
    }

    protected Environment getEnvironment() {
        return environment;
    }

    public STGroup getSTGroup() {
        return group;
    }

    protected void doGenerate(final io.github.potjerodekool.codegen.model.CompilationUnit compilationUnit,
                              final String fileName,
                              final Language language) {
        new io.github.potjerodekool.codegen.resolve.ImportOrganiser()
                .organiseImports(compilationUnit);

        final var enter = new Enter(environment.getSymbolTable());
        compilationUnit.accept(enter, null);

        new Resolver(environment.getElementUtils(), environment.getTypes(), environment.getSymbolTable())
                .resolve(compilationUnit);

        io.github.potjerodekool.codegen.model.CompilationUnit cu;

        if (language == Language.KOTLIN) {
            cu = new JavaToKotlinConverter(environment.getElementUtils(), environment.getTypes()).convert(compilationUnit);
        } else {
            cu = compilationUnit;
        }

        final var transformer = new AstToTemplateModelTransformer();
        final var unit = transformer.transform(cu);

        new ImportOrganiser().organiseImports(unit);

        doGenerate(unit, resolveFileName(fileName, language));
    }

    private String resolveFileName(final String fileName,
                                  final Language language) {
        if (language == Language.KOTLIN) {
            final var sepIndex = fileName.lastIndexOf('.');
            final var fileNameWithoutExtension = sepIndex > 0 ? fileName.substring(0, sepIndex) : fileName;
            return fileNameWithoutExtension + ".kt";
        } else {
            return fileName;
        }
    }


    protected void doGenerate(final CompilationUnit cu,
                              final String fileName) {
        final var packageName = cu.getPackageName();
        final ST template;

        if (cu.getLanguage() == Language.JAVA) {
            template = getSTGroup().getInstanceOf("java/compilationUnit");
        } else if (cu.getLanguage() == Language.KOTLIN) {
            template = getSTGroup().getInstanceOf("kotlin/compilationUnit");
        } else {
            throw new UnsupportedOperationException("Unsupported language: " + cu.getLanguage());
        }

        template.add("compilationUnit", cu);

        final var result = template.render();

        final var resource = environment.getFiler().createResource(
                Location.SOURCE_OUTPUT,
                packageName,
                fileName
        );

        try (final var outputStream = resource.openOutputStream()) {
            outputStream.write(result.getBytes());
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public abstract void generate();
}
