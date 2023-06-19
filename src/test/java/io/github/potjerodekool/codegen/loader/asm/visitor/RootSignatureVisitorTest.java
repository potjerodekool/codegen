package io.github.potjerodekool.codegen.loader.asm.visitor;

import io.github.potjerodekool.codegen.loader.TypeElementLoader;
import io.github.potjerodekool.codegen.loader.asm.AsmTypeElementLoader;
import io.github.potjerodekool.codegen.loader.asm.ClassPath;
import io.github.potjerodekool.codegen.loader.asm.visitor.signature2.RootSignatureVisitor2;
import io.github.potjerodekool.codegen.model.element.ElementKind;
import io.github.potjerodekool.codegen.model.element.Name;
import io.github.potjerodekool.codegen.model.element.NestingKind;
import io.github.potjerodekool.codegen.model.element.QualifiedNameable;
import io.github.potjerodekool.codegen.model.symbol.ClassSymbol;
import io.github.potjerodekool.codegen.model.symbol.PackageSymbol;
import io.github.potjerodekool.codegen.model.util.QualifiedName;
import io.github.potjerodekool.codegen.model.util.SymbolTable;
import io.github.potjerodekool.codegen.model.util.type.JavaTypes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RootSignatureVisitorTest {

    private ClassSymbol createClassSymbol() {
        final var classSymbol = ClassSymbol.create(
                ElementKind.CLASS,
                Name.of("SomeClass"),
                NestingKind.TOP_LEVEL,
                null
        );
        return classSymbol;
    }

    private RootSignatureVisitor2 createVisitor(final ClassSymbol classSymbol) {
        final var loader = new TestTypeElementLoader();
        final var types = new JavaTypes(loader);
        return new RootSignatureVisitor2(
                Opcodes.ASM9,
                loader,
                types,
                classSymbol
        );
    }

    @Test
    void testWithArray() {
        final var classSymbol = createClassSymbol();
        final var visitor = createVisitor(classSymbol);
        final var signature = "Ljava/lang/Object;Ljava/security/PrivilegedAction<[Ljava/lang/Class<*>;>;";
        new SignatureReader(signature).accept(visitor);
        final var classSignature = classSymbol.toString();
        System.out.println(classSignature);

        assertEquals("SomeClass extends java.lang.Object implements java.security.PrivilegedAction<[java.lang.Class<?>>", classSignature);
    }

    @Test
    public void t() {
        final var classSymbol = createClassSymbol();
        final var visitor = createVisitor(classSymbol);
        final var signature = "<T:Ljava/lang/Object;>Ljava/lang/Object;";
        new SignatureReader(signature).accept(visitor);
        final var classSignature = classSymbol.toString();
        System.out.println(classSignature);

        assertEquals("SomeClass extends java.lang.Object implements java.security.PrivilegedAction<[java.lang.Class<?>>", classSignature);
    }

    // <T:Ljava/lang/Object;>Ljava/lang/Object;

}

class TestTypeElementLoader implements TypeElementLoader {

    @Override
    public ClassSymbol loadTypeElement(final String name) {
        final var className = name.replace('/', '.');
        final var qualifiedName = QualifiedName.from(className);
        final PackageSymbol packageSymbol;

        if (qualifiedName.packageName().isEmpty()) {
            packageSymbol = PackageSymbol.DEFAULT_PACKAGE;
        } else {
            packageSymbol = PackageSymbol.create(qualifiedName.packageName());
        }

        return ClassSymbol.create(ElementKind.CLASS, qualifiedName.simpleName(), NestingKind.TOP_LEVEL, packageSymbol);
    }

    @Override
    public ClassSymbol doLoadTypeElement(final String name) {
        throw new UnsupportedOperationException();
    }
}