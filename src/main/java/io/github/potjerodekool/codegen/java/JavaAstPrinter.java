package io.github.potjerodekool.codegen.java;

import io.github.potjerodekool.codegen.AbstractAstPrinter;
import io.github.potjerodekool.codegen.CodeContext;
import io.github.potjerodekool.codegen.model.element.*;
import io.github.potjerodekool.codegen.model.tree.MethodDeclaration;
import io.github.potjerodekool.codegen.model.tree.expression.ArrayInitializerExpression;
import io.github.potjerodekool.codegen.model.tree.expression.NewClassExpression;
import io.github.potjerodekool.codegen.model.tree.type.BoundKind;
import io.github.potjerodekool.codegen.model.tree.type.PrimitiveTypeExpression;
import io.github.potjerodekool.codegen.model.tree.statement.ClassDeclaration;
import io.github.potjerodekool.codegen.model.tree.statement.VariableDeclaration;
import io.github.potjerodekool.codegen.io.Printer;
import io.github.potjerodekool.codegen.model.tree.*;
import io.github.potjerodekool.codegen.model.tree.type.WildCardTypeExpression;
import io.github.potjerodekool.codegen.model.type.*;
import io.github.potjerodekool.codegen.model.util.Counter;
import io.github.potjerodekool.codegen.model.util.Elements;
import io.github.potjerodekool.codegen.model.util.QualifiedName;
import io.github.potjerodekool.codegen.model.util.type.Types;

import java.util.List;

public class JavaAstPrinter extends AbstractAstPrinter {

    public JavaAstPrinter(final Printer printer,
                          final Types types) {
        super(printer, types);
    }

    //Elements
    @Override
    public Void visitType(final TypeElement typeElement,
                          final CodeContext context) {
        throw new UnsupportedOperationException();
    }

    void visitPrimaryConstructor(final MethodDeclaration primaryConstructor,
                                 final CodeContext context) {
        visitMethodParameters(primaryConstructor.getParameters(), context);
    }

    @Override
    public Void visitVariable(final VariableElement variableElement,
                              final CodeContext context) {
        /*
        final var isField = variableElement.getKind() == ElementKind.FIELD;
        final var annotations = variableElement.getAnnotationMirrors();

        printAnnotations(annotations, isField, context);

        final var modifiers = variableElement.getModifiers();

        if (modifiers.size() > 0
                && isField) {
            printer.printIndent();
        }

        if (modifiers.size() > 0
                && annotations.size() > 0
                && variableElement.getKind() == ElementKind.PARAMETER) {
            printer.print(" ");
        }

        printModifiers(modifiers);

        if (annotations.size() > 0
                || modifiers.size() > 0) {
            printer.print(" ");
        }

        variableElement.asType().accept(this, context);
        printer.print(" ");
        printer.print(variableElement.getSimpleName());

        if (isField) {
            final var initExpression = ((VariableSymbol) variableElement).getInitExpression();
            if (initExpression != null) {
                printer.print(" = ");
                initExpression.accept(this, context);
            }
            printer.printLn(";");
        }
        */
        throw new UnsupportedOperationException();
    }

    @Override
    public Void visitExecutable(final ExecutableElement methodElement,
                                final CodeContext context) {
        throw new UnsupportedOperationException();
    }

    //Expressions
    @Override
    public Void visitVariableDeclaration(final VariableDeclaration variableDeclaration,
                                         final CodeContext context) {
        final var isField = variableDeclaration.getSymbol().getKind() == ElementKind.FIELD;
        final var modifiers = variableDeclaration.getModifiers();

        if (modifiers.size() > 0
                && isField) {
            printer.printIndent();
        }

        if (modifiers.size() > 0) {
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
        newClassExpression.getClassType().accept(this, context);
        printer.print("()");
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

        if (elementValues.size() > 0) {
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

        if (elementValues.size() > 0) {
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
    public Void visitVarType(final VarTypeImpl varType, final CodeContext codeContext) {
        printer.print("var");
        return null;
    }

    @Override
    public Void visitClassDeclaration(final ClassDeclaration classDeclaration, final CodeContext context) {
        printer.printIndent();

        final var annotations = classDeclaration.getAnnotations();

        printAnnotations(
                annotations,
                true,
                context
        );

        printModifiers(classDeclaration.getModifiers());

        if (!classDeclaration.getModifiers().isEmpty()) {
            printer.print(" ");
        }

        switch (classDeclaration.getKind()) {
            case CLASS -> printer.print("class ");
            case INTERFACE -> printer.print("interface ");
            case RECORD -> printer.print("record ");
        }

        printer.print(classDeclaration.getSimpleName());

        final var primaryConstructor = classDeclaration.getPrimaryConstructor();

        if (primaryConstructor != null) {
            visitPrimaryConstructor(primaryConstructor, context);
        }

        final var superType = classDeclaration.getExtending();

        if (superType != null) {
            printer.print(" extends ");
            superType.accept(this, context);
        }

        final var interfaces = classDeclaration.getImplementing();

        if (interfaces.size() > 0) {
            printer.print(" implements ");

            for (int interfaceIndex = 0; interfaceIndex < interfaces.size(); interfaceIndex++) {
                if (interfaceIndex > 0) {
                    printer.print(", ");
                }
                final var interfaceType = interfaces.get(interfaceIndex);
                interfaceType.accept(this, context);
            }
        }

        final var enclosedElements = classDeclaration.getEnclosed();

        printer.printLn(" {");
        printer.indent();

        printer.printLn();
        final var lastIndex = enclosedElements.size() - 1;

        for (int i = 0; i < enclosedElements.size(); i++) {
            final var enclosedElement = enclosedElements.get(i);

            enclosedElement.accept(this, context);

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
        final var annotations = methodDeclaration.getAnnotations();

        if (annotations.size() > 0) {
            printAnnotations(annotations, true, context);
            printer.print(" ");
        }

        final var modifiers = methodDeclaration.getModifiers();

        if (modifiers.size() > 0) {
            printer.printIndent();
            printModifiers(modifiers);
            printer.print(" ");
        }

        printer.printIndent();

        if (methodDeclaration.getKind() != ElementKind.CONSTRUCTOR) {
            methodDeclaration.getReturnType().getType().accept(this, context);
            printer.print(" ");
        }

        printer.print(methodDeclaration.getSimpleName());

        visitMethodParameters(methodDeclaration.getParameters(), context);

        methodDeclaration.getBody().ifPresent(body -> body.accept(this, context));
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
}
