package io.github.potjerodekool.codegen.template;

import io.github.potjerodekool.codegen.Language;
import io.github.potjerodekool.codegen.ResourceLoader;
import io.github.potjerodekool.codegen.model.element.ElementKind;
import io.github.potjerodekool.codegen.model.element.Modifier;
import io.github.potjerodekool.codegen.template.model.TCompilationUnit;
import io.github.potjerodekool.codegen.template.model.element.MethodElem;
import io.github.potjerodekool.codegen.template.model.element.TypeElem;
import io.github.potjerodekool.codegen.template.model.expression.SimpleTypeExpr;
import io.github.potjerodekool.codegen.template.model.statement.BlockStm;
import io.github.potjerodekool.codegen.template.model.statement.VariableDeclarationStm;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VarDeclarationStmTest extends TemplateTest {

    @Test
    void test() {
        final var cu = new TCompilationUnit(Language.JAVA);
        final var clazz = new
                TypeElem()
                .kind(ElementKind.CLASS)
                .simpleName("SomeClass");

        final var method = new MethodElem()
                .kind(ElementKind.METHOD)
                .returnType(new SimpleTypeExpr("void"))
                .simpleName("someMethod");

        final var body = new BlockStm();
        method.body(body);

        body.statement(new VariableDeclarationStm()
                .modifier(Modifier.FINAL)
                .type(new SimpleTypeExpr("java.lang.String"))
                .identifier("myVar")
        );

        clazz.enclosedElement(method);

        cu.element(clazz);

        final var expected = ResourceLoader.load("template-test/VarDeclarationStmTest.java");

        final var actual = generateCode(cu);

        assertEquals(expected, actual);
    }
}
