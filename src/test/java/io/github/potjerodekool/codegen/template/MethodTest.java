package io.github.potjerodekool.codegen.template;

import io.github.potjerodekool.codegen.model.tree.type.BoundKind;
import io.github.potjerodekool.codegen.template.model.element.MethodElem;
import io.github.potjerodekool.codegen.template.model.type.ClassOrInterfaceTypeExpr;
import io.github.potjerodekool.codegen.template.model.type.WildCardTypeExpr;
import org.junit.jupiter.api.Test;

import static io.github.potjerodekool.codegen.ResourceLoader.load;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MethodTest extends TemplateTest {

    @Test
    void test() {


        final var returnType = new ClassOrInterfaceTypeExpr("ResponseEntity")
                .typeArgument(new WildCardTypeExpr()
                        .boundKind(BoundKind.EXTENDS)
                        .expr(new ClassOrInterfaceTypeExpr("Object")));

        final var method = new MethodElem()
                .returnType(returnType)
                .simpleName("someMethod");

        final var actual = generateCode(
                "/java/element/method",
                "method",
                method
        );

        final var expected = load("template-test/methodTest.java");
        assertEquals(expected, actual);
    }
}
