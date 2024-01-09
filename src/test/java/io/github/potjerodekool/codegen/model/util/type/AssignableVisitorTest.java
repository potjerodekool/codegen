package io.github.potjerodekool.codegen.model.util.type;

import io.github.potjerodekool.codegen.Environment;
import io.github.potjerodekool.codegen.loader.java.ClassPath;
import io.github.potjerodekool.codegen.model.type.immutable.PrimitiveTypeImpl;
import io.github.potjerodekool.codegen.model.type.TypeMirror;
import io.github.potjerodekool.codegen.model.type.java.immutable.JavaNoneType;
import io.github.potjerodekool.codegen.model.type.java.immutable.JavaVoidType;
import io.github.potjerodekool.codegen.model.util.Elements;
import io.github.potjerodekool.codegen.model.util.type.check.AssignableVisitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class AssignableVisitorTest {

    private final Environment environment = new Environment(ClassPath.getJavaClassPath());
    private final Elements elements = environment.getKotlinElements();
    private final Types types = environment.getKotlinTypes();
    private final AssignableVisitor assignableVisitor = new AssignableVisitor();

    @BeforeEach
    void setup() {
        this.assignableVisitor.init(types);
    }

    @Test
    void visitType() {
        final var type1 = Mockito.mock(TypeMirror.class);
        final var type2 = Mockito.mock(TypeMirror.class);
        assertFalse(assignableVisitor.visit(type1, type2));
    }

    @Test
    void visitNoType() {
        assertTrue(assignableVisitor.visitNoType(JavaVoidType.INSTANCE, JavaVoidType.INSTANCE));
        assertTrue(assignableVisitor.visitNoType(JavaNoneType.INSTANCE, JavaNoneType.INSTANCE));
        //assertFalse(assignableVisitor.visitNoType(JavaNoneType.INSTANCE, new PackageType(PackageSymbol.DEFAULT_PACKAGE)));

    }

    @Test
    void visitPrimitive() {
        final var primitiveTypes = List.of(
                PrimitiveTypeImpl.BOOLEAN,
                PrimitiveTypeImpl.BYTE,
                PrimitiveTypeImpl.SHORT,
                PrimitiveTypeImpl.INT,
                PrimitiveTypeImpl.LONG,
                PrimitiveTypeImpl.CHAR,
                PrimitiveTypeImpl.FLOAT,
                PrimitiveTypeImpl.DOUBLE
        );

        primitiveTypes.forEach(left -> {
            primitiveTypes.forEach(right -> {
                if (left.getKind() == right.getKind()) {
                    assertTrue(assignableVisitor.visitPrimitive(left, right));
                } else {
                    assertFalse(assignableVisitor.visitPrimitive(left, right));
                }
            });
        });
    }

    @Test
    void visitArray() {
    }

    @Test
    void visitDeclared() {
        final var arrayListElement = elements.getTypeElement("java.util.ArrayList");
        final var listElement = elements.getTypeElement("java.util.List");

        final var stringType = types.getDeclaredType(elements.getTypeElement("java.lang.String"));
        final var objectType = types.getDeclaredType(elements.getTypeElement("java.lang.Object"));

        //ArrayList<String>
        final var arrayListTypeWithString = types.getDeclaredType(arrayListElement, stringType);
        //List<String>
        final var listTypeWithString = types.getDeclaredType(listElement, stringType);
        final var listTypeWithObject = types.getDeclaredType(listElement, objectType);

        assertTrue(assignableVisitor.visitDeclared(arrayListTypeWithString, arrayListTypeWithString));
        assertTrue(assignableVisitor.visitDeclared(arrayListTypeWithString, listTypeWithString));
        assertTrue(assignableVisitor.visitDeclared(arrayListTypeWithString, listTypeWithObject));
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