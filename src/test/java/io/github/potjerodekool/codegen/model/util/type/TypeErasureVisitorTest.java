package io.github.potjerodekool.codegen.model.util.type;

import io.github.potjerodekool.codegen.Environment;
import io.github.potjerodekool.codegen.loader.java.ClassPath;
import io.github.potjerodekool.codegen.model.type.DeclaredType;
import io.github.potjerodekool.codegen.model.util.Elements;
import org.junit.jupiter.api.Test;

import static io.github.potjerodekool.codegen.model.element.Name.getQualifiedNameOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TypeErasureVisitorTest {

    private final TypeErasureVisitor typeErasureVisitor = new TypeErasureVisitor();
    private final Environment environment = new Environment(ClassPath.getJavaClassPath());
    private final Elements elements = environment.getElementUtils();
    private final Types types = environment.getTypes();

    @Test
    void visitType() {
    }

    @Test
    void visitNoType() {
    }

    @Test
    void visitPrimitive() {
    }

    @Test
    void visitArray() {
    }

    @Test
    void visitDeclared() {
        final var listElement = elements.getTypeElement("java.util.List");
        final var stringType = types.getDeclaredType(elements.getTypeElement("java.lang.String"));
        final var listStringType = types.getDeclaredType(listElement, stringType);
        final var listType = (DeclaredType)  listStringType.accept(typeErasureVisitor, null);
        assertEquals("java.util.List", getQualifiedNameOf(listType.asElement()).toString());
        assertTrue(listType.getTypeArguments().isEmpty());
    }

    @Test
    void visitError() {
    }

    @Test
    void visitNull() {
    }

    @Test
    void visitWildcard() {
    }

    @Test
    void visitExecutable() {
    }
}