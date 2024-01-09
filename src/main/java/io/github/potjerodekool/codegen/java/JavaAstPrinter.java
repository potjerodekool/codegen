package io.github.potjerodekool.codegen.java;

import io.github.potjerodekool.codegen.AbstractAstPrinter;
import io.github.potjerodekool.codegen.CodeContext;
import io.github.potjerodekool.codegen.model.element.*;
import io.github.potjerodekool.codegen.model.tree.MethodDeclaration;
import io.github.potjerodekool.codegen.model.tree.expression.ArrayInitializerExpression;
import io.github.potjerodekool.codegen.model.tree.expression.NewClassExpression;
import io.github.potjerodekool.codegen.model.tree.statement.ClassDeclaration;
import io.github.potjerodekool.codegen.model.tree.type.BoundKind;
import io.github.potjerodekool.codegen.model.tree.type.ClassOrInterfaceTypeExpression;
import io.github.potjerodekool.codegen.model.tree.type.PrimitiveTypeExpression;
import io.github.potjerodekool.codegen.model.tree.statement.VariableDeclaration;
import io.github.potjerodekool.codegen.io.Printer;
import io.github.potjerodekool.codegen.model.tree.*;
import io.github.potjerodekool.codegen.model.tree.type.WildCardTypeExpression;
import io.github.potjerodekool.codegen.model.type.*;
import io.github.potjerodekool.codegen.model.type.immutable.WildcardType;
import io.github.potjerodekool.codegen.model.util.Counter;
import io.github.potjerodekool.codegen.model.util.Elements;
import io.github.potjerodekool.codegen.model.util.QualifiedName;
import io.github.potjerodekool.codegen.model.util.type.Types;

import java.util.List;

public class JavaAstPrinter extends AbstractAstPrinter
    implements TreeVisitor<Void, CodeContext> {

    public JavaAstPrinter(final Printer printer,
                          final Types types) {
        super(printer, types);
    }

    /*
    //Elements
     */

    void visitPrimaryConstructor(final MethodDeclaration primaryConstructor,
                                 final CodeContext context) {
        visitMethodParameters(primaryConstructor.getParameters(), context);
    }

    //Expressions

    @Override
    public Void visitVariableDeclaration(final VariableDeclaration variableDeclaration,
                                         final CodeContext context) {
        final var isField = variableDeclaration.getSymbol().getKind() == ElementKind.FIELD;

        final var annotations = variableDeclaration.getAnnotations();

        if (!annotations.isEmpty()) {
            printAnnotations(annotations, true, false, context);
            printer.print(" ");
        }

        final var modifiers = variableDeclaration.getModifiers();

        if (!modifiers.isEmpty()
                && isField) {
            printer.printIndent();
        }

        if (!modifiers.isEmpty()) {
            printModifiers(modifiers);
            printer.print(" ");
        }

        variableDeclaration.getVarType().accept(this, context);
        printer.print(" ");

        printer.print(variableDeclaration.getName());

        variableDeclaration.getInitExpression().ifPresent(initExpression -> {
            printer.print(" = ");
            initExpression.accept(this, context);
        });

        if (variableDeclaration.getSymbol().getKind() == ElementKind.FIELD) {
            printer.print(";");
        }

        return null;
    }

    @Override
    public Void visitNewClassExpression(final NewClassExpression newClassExpression,
                                        final CodeContext context) {
        printer.print("new ");
        newClassExpression.getClazz().accept(this, context);
        printer.print("(");
        printExpressionList(newClassExpression.getArguments(), ", ", context);
        printer.print(")");
        return null;
    }

    @Override
    public Void visitArrayInitializerExpression(final ArrayInitializerExpression arrayInitializerExpression,
                                                final CodeContext context) {
        final var values = arrayInitializerExpression.getValues();

        printer.print("{");
        final var lastIndex = values.size() - 1;

        for (int i = 0; i < values.size(); i++) {
            final var value = values.get(i);
            value.accept(this, context);
            if (i < lastIndex) {
                printer.print(", ");
            }
        }
        printer.print("}");

        return null;
    }

    @Override
    public Void visitType(final TypeMirror type, final CodeContext codeContext) {
        final var element = getTypes().asElement(type);

        if (element instanceof TypeElement te) {
            if (isInImport(te.getQualifiedName(), codeContext)) {
                printer.print(element.getSimpleName());
            } else {
                printer.print(te.getQualifiedName());
            }
        } else if (type instanceof PrimitiveType primitiveType) {
            primitiveType.accept(this, codeContext);
        }
        printer.print(".class");
        return null;
    }

    @Override
    public Void visitArray(final List<? extends AnnotationValue> array, final CodeContext param) {
        printer.print("{");

        final var lastIndex = array.size() - 1;

        for (int valueIndex = 0; valueIndex < array.size(); valueIndex++) {
            array.get(valueIndex).accept(this, param);
            if (valueIndex < lastIndex) {
                printer.print(", ");
            }
        }

        printer.print("}");

        return null;
    }

    @Override
    public Void visitAnnotation(final AnnotationMirror annotation,
                                final CodeContext context) {
        final var elementValues = annotation.getElementValues();

        printer.print("@");
        printer.print(resolveClassName(Elements.getQualifiedName(annotation.getAnnotationType().asElement()), context));

        if (!elementValues.isEmpty()) {
            printer.print("(");

            final var lastIndex = elementValues.size() - 1;
            final var counter = new Counter();

            elementValues.forEach((name,value) -> {
                printer.print(name.getSimpleName());
                printer.print(" = ");
                value.accept(this, context);

                if (counter.getValue() < lastIndex) {
                    printer.print(", ");
                }
                counter.increment();
            });
            printer.print(")");
        }

        return null;
    }

    @Override
    public Void visitAnnotationExpression(final AnnotationExpression annotationExpression, final CodeContext context) {
        final var elementValues = annotationExpression.getArguments();

        final var annotationType = (DeclaredType)  annotationExpression.getAnnotationType().getType();

        printer.print("@");
        printer.print(resolveClassName(Elements.getQualifiedName(annotationType.asElement()), context));

        if (!elementValues.isEmpty()) {
            printer.print("(");

            final var lastIndex = elementValues.size() - 1;
            final var counter = new Counter();

            elementValues.forEach((name,value) -> {
                printer.print(name);
                printer.print(" = ");
                value.accept(this, context);

                if (counter.getValue() < lastIndex) {
                    printer.print(", ");
                }
                counter.increment();
            });
            printer.print(")");
        }

        return null;
    }

    @Override
    public Void visitArray(final ArrayType javaArrayType,
                           final CodeContext context) {
        javaArrayType.getComponentType().accept(this, context);
        printer.print("[]");
        return null;
    }

    @Override
    public Void visitPrimitive(final PrimitiveType t, final CodeContext codeContext) {
        switch (t.getKind()) {
            case BOOLEAN -> printer.print("boolean");
            case BYTE -> printer.print("byte");
            case FLOAT -> printer.print("float");
            case DOUBLE -> printer.print("double");
            case SHORT -> printer.print("short");
            case LONG -> printer.print("long");
            case INT -> printer.print("int");
            case CHAR -> printer.print("char");
        }

        return null;
    }

    @Override
    public Void visitWildcard(final WildcardType wildcardType,
                              final CodeContext context) {
        final var extendsBound = wildcardType.getExtendsBound();
        final var superBound = wildcardType.getSuperBound();

        if (extendsBound != null) {
            printer.print("? extends ");
            extendsBound.accept(this, context);
        } else if (superBound != null) {
            printer.print("? super ");
            superBound.accept(this, context);
        } else {
            printer.print("?");
        }
        return null;
    }

    @Override
    public Void visitWildCardTypeExpression(final WildCardTypeExpression wildCardTypeExpression, final CodeContext context) {
        final var typeExpression = wildCardTypeExpression.getTypeExpression();

        if (wildCardTypeExpression.getBoundKind() == BoundKind.EXTENDS) {
            printer.print("? extends ");
            typeExpression.accept(this, context);
        } else if (wildCardTypeExpression.getBoundKind() == BoundKind.SUPER) {
            printer.print("? super ");
            typeExpression.accept(this, context);
        } else {
            printer.print("?");
        }

        return null;
    }

    @Override
    protected Name resolveClassName(final Name className, final CodeContext context) {
        final var qualifiedName = QualifiedName.from(className);
        if ("java.lang".equals(qualifiedName.packageName().toString())) {
            return qualifiedName.simpleName();
        }
        return super.resolveClassName(className, context);
    }

    @Override
    public Void visitVarType(final VarType varType, final CodeContext codeContext) {
        printer.print("var");
        return null;
    }

    @Override
    public Void visitClassDeclaration(final ClassDeclaration classDeclaration, final CodeContext context) {
        final var classContext = context.child(classDeclaration);

        printer.printIndent();

        final var annotations = classDeclaration.getAnnotations();

        printAnnotations(
                annotations,
                true,
                false,
                classContext
        );

        printModifiers(classDeclaration.getModifiers());

        if (!classDeclaration.getModifiers().isEmpty()) {
            printer.print(" ");
        }

        final ElementKind kind = classDeclaration.getKind();

        switch (kind) {
            case CLASS -> printer.print("class ");
            case INTERFACE -> printer.print("interface ");
            case RECORD -> printer.print("record ");
        }

        printer.print(classDeclaration.getSimpleName());

        final var primaryConstructor = classDeclaration.getPrimaryConstructor();

        if (primaryConstructor != null) {
            visitPrimaryConstructor(primaryConstructor, classContext);
        }

        final var superType = classDeclaration.getExtending();

        if (superType != null) {
            printer.print(" extends ");
            superType.accept(this, classContext);
        }

        final var interfaces = classDeclaration.getImplementing();

        if (!interfaces.isEmpty()) {
            printer.print(" implements ");

            for (int interfaceIndex = 0; interfaceIndex < interfaces.size(); interfaceIndex++) {
                if (interfaceIndex > 0) {
                    printer.print(", ");
                }
                final var interfaceType = interfaces.get(interfaceIndex);
                interfaceType.accept(this, classContext);
            }
        }

        final var enclosedElements = classDeclaration.getEnclosed();

        printer.printLn(" {");
        printer.indent();

        printer.printLn();
        final var lastIndex = enclosedElements.size() - 1;

        for (int i = 0; i < enclosedElements.size(); i++) {
            final var enclosedElement = enclosedElements.get(i);

            enclosedElement.accept(this, classContext);

            if (i < lastIndex) {
                printer.printLn();
            }
        }

        printer.deIndent();
        printer.printLn("}");
        printer.deIndent();
        return null;
    }

    @Override
    public Void visitMethodDeclaration(final MethodDeclaration methodDeclaration,
                                       final CodeContext context) {
        final var methodContext = context.child(methodDeclaration);

        final var annotations = methodDeclaration.getAnnotations();

        if (!annotations.isEmpty()) {
            printAnnotations(annotations, true, true, methodContext);
            printer.print(" ");
        }

        final var modifiers = methodDeclaration.getModifiers();

        if (!modifiers.isEmpty()) {
            printer.printIndent();
            printModifiers(modifiers);
            printer.print(" ");
        }

        if (methodDeclaration.getKind() != ElementKind.CONSTRUCTOR) {
            methodDeclaration.getReturnType().getType().accept(this, methodContext);
            printer.print(" ");
        }

        printer.print(methodDeclaration.getSimpleName());

        visitMethodParameters(methodDeclaration.getParameters(), methodContext);

        final var bodyOptional = methodDeclaration.getBody();

        if (bodyOptional.isPresent()) {
            final var body = bodyOptional.get();
            printer.print(" ");
            body.accept(this, methodContext);
        } else {
            printer.printLn(";");
        }

        return null;
    }

    @Override
    public Void visitTypeParameter(final TypeParameter typeParameter, final CodeContext param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Void visitPrimitiveTypeExpression(final PrimitiveTypeExpression primitiveTypeExpression, final CodeContext param) {
        primitiveTypeExpression.getType().accept(this, param);
        return null;
    }

    @Override
    public Void visitClassOrInterfaceTypeExpression(final ClassOrInterfaceTypeExpression classOrInterfaceTypeExpression,
                                                    final CodeContext context) {
        final var type = classOrInterfaceTypeExpression.getType();

        if (type instanceof ClassType classType) {
            final var classSymbol = classType.asElement();
            final var resolvedClassName = resolveClassName(classSymbol.getQualifiedName(), context);
            printer.print(resolvedClassName);
        } else {
            printer.print(classOrInterfaceTypeExpression.getName());
        }

        final var arguments = classOrInterfaceTypeExpression.getTypeArguments();

        if (arguments != null) {
            printer.print("<");
            final var lastIndex = arguments.size() -1;

            for (int i = 0; i < arguments.size(); i++) {
                arguments.get(i).accept(this, context);

                if (i < lastIndex) {
                    printer.print(",");
                }
            }

            printer.print(">");
        }

        return null;
    }

}
