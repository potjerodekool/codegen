package io.github.potjerodekool.codegen.template;

import io.github.potjerodekool.codegen.Language;
import io.github.potjerodekool.codegen.ResourceLoader;
import io.github.potjerodekool.codegen.model.element.ElementKind;
import io.github.potjerodekool.codegen.template.model.TCompilationUnit;
import io.github.potjerodekool.codegen.template.model.annotation.Annot;
import io.github.potjerodekool.codegen.template.model.element.TypeElem;
import io.github.potjerodekool.codegen.template.model.element.VariableElem;
import io.github.potjerodekool.codegen.template.model.type.ClassOrInterfaceTypeExpr;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FieldTest extends TemplateTest {

    @Test
    void test() {
        final var cu = new TCompilationUnit(Language.JAVA);
        final var clazz = new
                TypeElem()
                .kind(ElementKind.CLASS)
                .simpleName("SomeClass")
                .enclosedElement(
                        new VariableElem()
                                .kind(ElementKind.FIELD)
                                .type(new ClassOrInterfaceTypeExpr("java.lang.String"))
                                .simpleName("someField")
                                .annotation(new Annot("org.some.Annot"))
                );

        cu.element(clazz);
        final var actual = generateCode(cu);
        final var expected = ResourceLoader.load("template-test/fieldTest.java");
        assertEquals(expected, actual);
    }

}
