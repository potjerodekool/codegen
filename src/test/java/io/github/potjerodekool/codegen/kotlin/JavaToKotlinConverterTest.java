package io.github.potjerodekool.codegen.kotlin;

import io.github.potjerodekool.codegen.Language;
import io.github.potjerodekool.codegen.model.CompilationUnit;
import io.github.potjerodekool.codegen.model.element.ElementKind;
import io.github.potjerodekool.codegen.model.element.Modifier;
import io.github.potjerodekool.codegen.model.element.Name;
import io.github.potjerodekool.codegen.model.tree.MethodDeclaration;
import io.github.potjerodekool.codegen.model.tree.statement.ClassDeclaration;
import io.github.potjerodekool.codegen.model.tree.statement.VariableDeclaration;
import io.github.potjerodekool.codegen.model.tree.type.ClassOrInterfaceTypeExpression;
import io.github.potjerodekool.codegen.model.tree.type.NoTypeExpression;
import io.github.potjerodekool.codegen.model.type.TypeKind;
import io.github.potjerodekool.codegen.model.util.Elements;
import io.github.potjerodekool.codegen.model.util.type.Types;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JavaToKotlinConverterTest {

    private JavaToKotlinConverter converter;

    @Mock
    private Elements elements;

    @Mock
    private Types types;

    @BeforeEach
    void setup() {
        converter = new JavaToKotlinConverter(
                elements,
                types
        );
    }

    @Test
    void convert() {
        final var compilationUnit = new CompilationUnit(Language.JAVA);

        final var classDeclaration = new ClassDeclaration()
                .simpleName(Name.of("ErrorDto"))
                .kind(ElementKind.CLASS);

        classDeclaration.addEnclosed(new VariableDeclaration()
                        .kind(ElementKind.FIELD)
                        .varType(new ClassOrInterfaceTypeExpression("java.lang.String"))
                        .name("code")
        );

        final var constructor = new MethodDeclaration()
                .simpleName(Name.of("ErrorDto"))
                .kind(ElementKind.CONSTRUCTOR)
                .returnType(new NoTypeExpression(TypeKind.VOID))
                .parameter(new VariableDeclaration()
                                .kind(ElementKind.PARAMETER)
                                .varType(new ClassOrInterfaceTypeExpression("java.lang.String"))
                                .name("code")
                );

        classDeclaration.addEnclosed(constructor);

        compilationUnit.classDeclaration(classDeclaration);

        converter.convert(compilationUnit);
    }

    @Test
    void visitMethodDeclaration() {
    }

    @Test
    void visitUnknown() {
    }

    @Test
    void visitExpressionStatement() {
    }

    @Test
    void visitBlockStatement() {
    }

    @Test
    void visitReturnStatement() {
    }

    @Test
    void visitIfStatement() {
    }

    @Test
    void testVisitUnknown() {
    }

    @Test
    void visitMethodCall() {
    }

    @Test
    void visitVariableDeclaration() {
    }

    @Test
    void visitNamedMethodArgumentExpression() {
    }

    @Test
    void visitIdentifierExpression() {
    }

    @Test
    void visitFieldAccessExpression() {
    }

    @Test
    void visitBinaryExpression() {
    }

    @Test
    void visitLiteralExpression() {
    }

    @Test
    void visitAnnotation() {
    }

    @Test
    void visitArrayAccessExpression() {
    }

    @Test
    void visitArrayInitializerExpression() {
    }

    @Test
    void visitNewClassExpression() {
    }

    @Test
    void visitAnnotatedType() {
    }

    @Test
    void testVisitUnknown1() {
    }

    @Test
    void visitClassOrInterfaceTypeExpression() {
    }

    @Test
    void visitDeclared() {
    }

    @Test
    void visitExecutable() {
    }

    @Test
    void visitArray() {
    }

    @Test
    void visitPrimitive() {
    }

    @Test
    void visitWildcard() {
    }

    @Test
    void visit() {
    }

    @Test
    void testVisit() {
    }

    @Test
    void visitBoolean() {
    }

    @Test
    void visitByte() {
    }

    @Test
    void visitChar() {
    }

    @Test
    void visitShort() {
    }

    @Test
    void visitInt() {
    }

    @Test
    void visitLong() {
    }

    @Test
    void visitFloat() {
    }

    @Test
    void visitDouble() {
    }

    @Test
    void visitString() {
    }

    @Test
    void visitType() {
    }

    @Test
    void testVisitUnknown2() {
    }

    @Test
    void visitEnumConstant() {
    }

    @Test
    void testVisitArray() {
    }

    @Test
    void testVisit1() {
    }

    @Test
    void visitNull() {
    }

    @Test
    void visitNoType() {
    }

    @Test
    void visitError() {
    }

    @Test
    void visitTypeVariable() {
    }

    @Test
    void visitUnion() {
    }

    @Test
    void visitIntersection() {
    }

    @Test
    void visitVarType() {
    }

    @Test
    void visitClassDeclaration() {
        final var classDeclaration = new ClassDeclaration()
                .simpleName(Name.of("SomeClass"))
                .kind(ElementKind.CLASS)
                .modifiers(Modifier.PUBLIC, Modifier.FINAL);
        final var result = classDeclaration.accept(converter, null);
        System.out.println(result);
    }

    @Test
    void visitPrimitiveTypeExpression() {
    }

    @Test
    void visitParameterizedType() {
    }

    @Test
    void testVisitNoType() {
    }

    @Test
    void visitAnnotationExpression() {
    }

    @Test
    void visitWildCardTypeExpression() {
    }
}