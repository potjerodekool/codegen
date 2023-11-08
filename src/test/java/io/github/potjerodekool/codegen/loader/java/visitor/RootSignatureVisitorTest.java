package io.github.potjerodekool.codegen.loader.java.visitor;

import io.github.potjerodekool.codegen.loader.TypeElementLoader;
import io.github.potjerodekool.codegen.loader.java.visitor.signature.LogSignatureVisitor;
import io.github.potjerodekool.codegen.loader.java.visitor.signature.TypeBuilder;
import io.github.potjerodekool.codegen.model.element.ElementKind;
import io.github.potjerodekool.codegen.model.element.NestingKind;
import io.github.potjerodekool.codegen.model.symbol.ClassSymbol;
import io.github.potjerodekool.codegen.model.symbol.PackageSymbol;
import io.github.potjerodekool.codegen.model.util.ElementsImpl;
import io.github.potjerodekool.codegen.model.util.QualifiedName;
import io.github.potjerodekool.codegen.model.util.SymbolTable;
import io.github.potjerodekool.codegen.model.util.type.JavaTypes;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RootSignatureVisitorTest {

    private ClassSymbol createClassSymbol() {
        return new ClassSymbol(ElementKind.CLASS, "SomeClass", NestingKind.TOP_LEVEL, null);
    }

    private SignatureVisitor createVisitor(final ClassSymbol classSymbol) {
        final var loader = new TestTypeElementLoader();
        final var symbolTable = new SymbolTable();
        final var types = new JavaTypes(symbolTable);
        final var elements = new ElementsImpl(symbolTable);

        return new TypeBuilder(
                Opcodes.ASM9,
                elements,
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

        final var printer = new ClassSignaturePrinter();
        classSymbol.accept(printer, null);
        final var classSignature = printer.getSignature();

        assertEquals("SomeClass<T extends java.lang.Object> extends java.lang.Object", classSignature);
    }

    @Test
    public void t2() {
        final var classSymbol = createClassSymbol();
        final var visitor = createVisitor(classSymbol);
        final var signature = "<T:Ljava/lang/Object;A:Ljava/lang/Object;>Ljava/lang/Object;";
        new SignatureReader(signature).accept(visitor);

        final var printer = new ClassSignaturePrinter();
        classSymbol.accept(printer, null);
        final var classSignature = printer.getSignature();

        System.out.println(classSignature);

        assertEquals("SomeClass<T extends java.lang.Object,A extends java.lang.Object> extends java.lang.Object", classSignature);
    }

    @Test
    public void t3() {
        final var classSymbol = createClassSymbol();
        final var visitor = createVisitor(classSymbol);
        final var signature = "Ljava/lang/Object;Ljava/lang/Comparable<Ljava/net/URI;>;Ljava/io/Serializable;";
        new SignatureReader(signature).accept(visitor);

        final var printer = new ClassSignaturePrinter();
        classSymbol.accept(printer, null);
        final var classSignature = printer.getSignature();

        System.out.println(classSignature);

        assertEquals("SomeClass extends java.lang.Object implements java.lang.Comparable<java.net.URI>, java.io.Serializable", classSignature);
    }

    @Test
    public void t4() {
        final var classSymbol = createClassSymbol();
        final var visitor = createVisitor(classSymbol);
        final var signature = "<E:Ljava/lang/Enum<TE;>;>Ljava/lang/Object;Ljava/lang/constant/Constable;Ljava/lang/Comparable<TE;>;Ljava/io/Serializable;";
        new SignatureReader(signature).accept(visitor);

        final var printer = new ClassSignaturePrinter();
        classSymbol.accept(printer, null);
        final var classSignature = printer.getSignature();

        System.out.println(classSignature);

        assertEquals("SomeClass<E extends java.lang.Enum<E>> extends java.lang.Object implements java.lang.constant.Constable, java.lang.Comparable<E>, java.io.Serializable", classSignature);
    }

    private void testSignature(
            final String expected,
            final String signature) {
        final var classSymbol = createClassSymbol();
        final var visitor = createVisitor(classSymbol);
        new SignatureReader(signature).accept(visitor);

        final var printer = new ClassSignaturePrinter();
        classSymbol.accept(printer, null);
        final var classSignature = printer.getSignature();

        System.out.println(classSignature);

        assertEquals(expected, classSignature);
    }

    @Test
    public void t5() {
        testSignature("SomeClass extends java.lang.ref.WeakReference<java.lang.ThreadLocal<?>>", "Ljava/lang/ref/WeakReference<Ljava/lang/ThreadLocal<*>;>;");
    }

    @Test
    void t6() {
        testSignature("SomeClass extends java.lang.Object implements java.security.PrivilegedAction<[java.lang.Class<?>>", "Ljava/lang/Object;Ljava/security/PrivilegedAction<[Ljava/lang/Class<*>;>;");
    }

    @Test
    void t7() {
        final var visitor = new LogSignatureVisitor(Opcodes.ASM9);
        final var signature = "<U:Ljava/lang/Object;T:TU;>Ljava/util/concurrent/CompletableFuture$UniCompletion<TT;TU;>;";
        new SignatureReader(signature).accept(visitor);

        testSignature("SomeClass<U extends java.lang.Object,T extends U> extends java.util.concurrent.CompletableFuture$UniCompletion<T,U>", "<U:Ljava/lang/Object;T:TU;>Ljava/util/concurrent/CompletableFuture$UniCompletion<TT;TU;>;");
    }

    @Test
    void t8() {
        final var visitor = new LogSignatureVisitor(Opcodes.ASM9);
        final var signature = "<K:Ljava/lang/Object;V:Ljava/lang/Object;>Ljava/lang/Object;Ljava/util/Map<TK;TV;>;";
        new SignatureReader(signature).accept(visitor);

        testSignature("SomeClass<U extends java.lang.Object,T extends U> extends java.util.concurrent.CompletableFuture$UniCompletion<T,U>", "<U:Ljava/lang/Object;T:TU;>Ljava/util/concurrent/CompletableFuture$UniCompletion<TT;TU;>;");
    }

}

class TestTypeElementLoader implements TypeElementLoader {

    @Override
    public ClassSymbol loadTypeElement(final String name) {
        final var className = name.replace('/', '.');
        final var qualifiedName = QualifiedName.from(className);
        final PackageSymbol packageSymbol;

        if (qualifiedName.packageName().isEmpty()) {
            //packageSymbol = PackageSymbol.DEFAULT_PACKAGE;
            throw new UnsupportedOperationException();
        } else {
            //packageSymbol = PackageSymbol.create(qualifiedName.packageName());
            throw new UnsupportedOperationException();
        }

        /*
        ElementKind elementKind = ElementKind.CLASS;

        try {
            final var clazz = getClass().getClassLoader().loadClass(className);
            if (clazz.isInterface()) {
                elementKind = ElementKind.INTERFACE;
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return new JClassSymbol(elementKind,
                qualifiedName.simpleName(),
                NestingKind.TOP_LEVEL,
                packageSymbol
                );
        */
    }
}