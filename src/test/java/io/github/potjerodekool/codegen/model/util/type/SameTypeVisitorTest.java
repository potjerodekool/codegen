package io.github.potjerodekool.codegen.model.util.type;

import io.github.potjerodekool.codegen.model.type.immutable.PrimitiveTypeImpl;
import io.github.potjerodekool.codegen.model.type.TypeKind;
import io.github.potjerodekool.codegen.model.util.type.check.SameTypeVisitor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SameTypeVisitorTest {

    @Test
    void testPrimitive() {
        final var type = new PrimitiveTypeImpl(TypeKind.BOOLEAN);

        final var visitor = new SameTypeVisitor();
        assertTrue(visitor.visit(type, type));
    }

}