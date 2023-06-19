package io.github.potjerodekool.codegen.kotlin;

import io.github.potjerodekool.codegen.AbstractAstPrinter;
import io.github.potjerodekool.codegen.CodeContext;
import io.github.potjerodekool.codegen.model.tree.AnnotationExpression;
import io.github.potjerodekool.codegen.model.tree.expression.*;
import io.github.potjerodekool.codegen.io.Printer;
import io.github.potjerodekool.codegen.model.element.*;
import io.github.potjerodekool.codegen.model.tree.type.PrimitiveTypeExpression;
import io.github.potjerodekool.codegen.model.tree.statement.IfStatement;
import io.github.potjerodekool.codegen.model.tree.statement.ClassDeclaration;
import io.github.potjerodekool.codegen.model.tree.MethodDeclaration;
import io.github.potjerodekool.codegen.model.tree.TypeParameter;
import io.github.potjerodekool.codegen.model.tree.statement.VariableDeclaration;
import io.github.potjerodekool.codegen.model.type.*;
import io.github.potjerodekool.codegen.model.type.java.JavaArrayType;
import io.github.potjerodekool.codegen.model.type.kotlin.UnitType;
import io.github.potjerodekool.codegen.model.util.Counter;
import io.github.potjerodekool.codegen.model.util.Elements;
import io.github.potjerodekool.codegen.model.util.QualifiedName;
import io.github.potjerodekool.codegen.model.util.type.Types;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class KotlinAstPrinter extends AbstractAstPrinter {

    public KotlinAstPrinter(final Printer printer,
                            final Types types) {
        super(printer, types);
    }

    //Elements
    @Override
    public Void visitType(final TypeElement typeElement,
                          final CodeContext context) {
        throw new UnsupportedOperationException();
    }

    void visitPrimaryConstructor(final MethodDeclaration method,
                                 final CodeContext context) {
        if (method.getAnnotations().size() > 0) {
            printer.print(" ");
            throw new UnsupportedOperationException();
            /*TODO
            printAnnotations(
                    method.getAnnotationMirrors(),
                    false,
                    context
            );

            printer.print(" constructor");
             */
        }
        visitMethodParameters(method.getParameters(), context);
    }

    @Override
    public Void visitExecutable(final ExecutableElement methodElement,
                                final CodeContext context) {
        throw new UnsupportedOperationException();
    }

    private void visitMethod(final ExecutableElement methodElement,
                             final CodeContext context) {
        throw new UnsupportedOperationException();
    }


    @Override
    public Void visitVariable(final VariableElement variableElement,
                              final CodeContext context) {
        throw new UnsupportedOperationException();
    }

    /*
    @Override
    public Void visitVariable(final VariableElement variableElement,
                              final CodeContext context) {
        final var isField = variableElement.getKind() == ElementKind.FIELD;
        final var annotations = variableElement.getAnnotationMirrors();
        final var modifiers = variableElement.getModifiers();

        final var hasAnnotations = annotations.size() > 0;
        final var hasModifiers = modifiers.size() > 0;

        if (hasAnnotations
                && isField) {
            printer.printIndent();
        }

        printAnnotations(variableElement.getAnnotationMirrors(), isField, context);

        if (hasModifiers
                && isField) {
            printer.printIndent();
        }

        if (hasModifiers
                && hasAnnotations
                && variableElement.getKind() == ElementKind.PARAMETER) {
            printer.print(" ");
        }

        printModifiers(modifiers);

        if (hasAnnotations
                || hasModifiers) {
            printer.print(" ");
        }

        printer.print(variableElement.getSimpleName());
        printer.print(" : ");

        final var variableType = variableElement.asType();

        variableType.accept(this, context);

        final var initExpression = ((VariableSymbol)variableElement).getInitExpression();

        if (initExpression != null) {
            printer.print(" = ");
            initExpression.accept(this, context);
        }
        return null;
    }
    */

    //Expressions
    @Override
    public Void visitLiteralExpression(final LiteralExpression literalExpression,
                                       final CodeContext context) {
        if (literalExpression.getLiteralType() == LiteralType.CLASS) {
            final var classLiteralExpression = (ClassLiteralExpression) literalExpression;
            final var type = (DeclaredType) classLiteralExpression.getType();
            printer.print(resolveClassName(type.asElement(), context) + "::class");
            return null;
        } else {
            return super.visitLiteralExpression(literalExpression, context);
        }
    }

    @Override
    public Void visitNamedMethodArgumentExpression(final NamedMethodArgumentExpression namedMethodArgumentExpression,
                                                   final CodeContext context) {
        final var name = namedMethodArgumentExpression.getName();
        final var argument = namedMethodArgumentExpression.getArgument();

        printer.print(name);
        printer.print(" ");
        argument.accept(this, context);
        return null;
    }

    @Override
    public Void visitVariableDeclaration(final VariableDeclaration variableDeclaration,
                                         final CodeContext context) {
        if (variableDeclaration.getModifiers().contains(Modifier.FINAL)) {
            printer.print("val");
        } else {
            printer.print("var");
        }

        final var modifiers = variableDeclaration.getModifiers();

        if (modifiers.size() > 0) {
            printer.print(" ");
        }

        printModifiers(modifiers);
        printer.print(" ");
        printer.print(variableDeclaration.getName());

        variableDeclaration.getInitExpression().ifPresent(initExpression -> {
            printer.print(" = ");
            initExpression.accept(this, context);
        });
        printer.printLn();
        return null;
    }


    @Override
    public Void visitNewClassExpression(final NewClassExpression newClassExpression,
                                        final CodeContext context) {
        newClassExpression.getClassType().accept(this, context);
        printer.print("()");
        return null;
    }

    @Override
    public Void visitArrayInitializerExpression(final ArrayInitializerExpression arrayInitializerExpression,
                                                final CodeContext context) {
        final var literalTypeOptional = detectTypeOfValues(arrayInitializerExpression);
        final String arrayOfMethodName;

        if (literalTypeOptional.isPresent()) {
            final var literalType = literalTypeOptional.get();
            arrayOfMethodName = switch (literalType) {
                case BYTE -> "byteArrayOf";
                case CHAR -> "charArrayOf";
                case SHORT -> "shortArrayOf";
                case INT -> "intArrayOf";
                case LONG -> "longArrayOf";
                case FLOAT -> "floatArrayOf";
                case DOUBLE -> "doubleArrayOf";
                case BOOLEAN -> "booleanArrayOf";
                default -> "arrayOf";
            };
        } else {
            arrayOfMethodName = "arrayOf";
        }

        printer.print(arrayOfMethodName);
        printer.print("(");

        final var values = arrayInitializerExpression.getValues();

        final var lastIndex = values.size() -1 ;

        final var childContext = context.child(arrayInitializerExpression);

        for (int i = 0; i < values.size(); i++) {
            final var value = values.get(i);
            value.accept(this, childContext);
            if (i < lastIndex) {
                printer.print(", ");
            }
        }
        printer.print(")");
        return null;
    }

    private Optional<LiteralType> detectTypeOfValues(final ArrayInitializerExpression arrayInitializerExpression) {
        final var values = arrayInitializerExpression.getValues();

        if (values.size() > 0) {
            final var firstValue = values.get(0);
            if (firstValue instanceof LiteralExpression le) {
                return Optional.of(le.getLiteralType());
            }
        }

        return Optional.empty();
    }

    private boolean isPartOfAnnotationExpressionOrArrayInitializerExpression(final CodeContext context) {
        final var astNode = context.getAstNode();

        if ((astNode instanceof AnnotationMirror || astNode instanceof ArrayInitializerExpression)) {
            return true;
        }

        final var parentContext = context.getParentContext();
        return parentContext != null && isPartOfAnnotationExpressionOrArrayInitializerExpression(parentContext);
    }

    @Override
    public Void visitAnnotation(final AnnotationMirror annotationExpression,
                                final CodeContext context) {
        final var elementValues = annotationExpression.getElementValues();

        if (!isPartOfAnnotationExpressionOrArrayInitializerExpression(context)) {
            printer.print("@");
        }

        final var className = resolveClassName(
                Elements.getQualifiedName(annotationExpression.getAnnotationType().asElement()),
                context);

        printer.print(className);

        if (elementValues.size() > 0) {
            printer.print("(");

            final var lastIndex = elementValues.size() - 1;
            final var counter = new Counter();

            final var childContext = context.child(annotationExpression);

            elementValues.forEach((name,value) -> {
                printer.print(name.getSimpleName());
                printer.print(" = ");
                value.accept(this, childContext);

                if (counter.getValue() < lastIndex) {
                    printer.print(", ");
                }
                counter.increment();
            });
            printer.print(")");
        }

        return null;
    }

    //Types
    @Override
    public Void visitDeclared(final DeclaredType declaredType,
                              final CodeContext context) {
        final var result = super.visitDeclared(declaredType, context);

        if (declaredType.isNullable()) {
            printer.print("?");
        }

        return result;
    }

    @Override
    public Void visitArray(final ArrayType t, final CodeContext codeContext) {
        if (t instanceof JavaArrayType) {
            printer.print("Array<");
            t.getComponentType().accept(this, codeContext);
            printer.print(">");
        } else {
            final var componentType = t.getComponentType();

            if (componentType.getKind() == TypeKind.DECLARED) {
                final var declaredComponentType = (DeclaredType) componentType;
                final var componentTypeName = Elements.getQualifiedName(declaredComponentType.asElement());
                final var isNullable = declaredComponentType.isNullable();

                if (isNullable) {
                    printer.print("Array<");
                    componentType.accept(this, codeContext);
                    printer.print(">?");
                } else {
                    switch (componentTypeName.toString()) {
                        case "kotlin.Byte" -> printer.print("ByteArray");
                        case "kotlin.Char" -> printer.print("CharArray");
                        case "kotlin.Short" -> printer.print("ShortArray");
                        case "kotlin.Int" -> printer.print("IntArray");
                        case "kotlin.Long" -> printer.print("LongArray");
                        case "kotlin.Float" -> printer.print("FloatArray");
                        case "kotlin.Double" -> printer.print("DoubleArray");
                        case "kotlin.Boolean" -> printer.print("BooleanArray");
                        default -> {
                            printer.print("Array<");
                            componentType.accept(this, codeContext);
                            printer.print(">");
                        }
                    }
                }
            } else {
                printer.print("Array<");
                componentType.accept(this, codeContext);
                printer.print(">");

                if (t.isNullable()) {
                    printer.print("?");
                }
            }
        }

        return null;
    }

    @Override
    public Void visitPrimitive(final PrimitiveType t,
                               final CodeContext codeContext) {
        switch (t.getKind()) {
            case CHAR -> {
                printer.print("Char");
                if (t.isNullable()) {
                    printer.print("?");
                }
            }
            case DOUBLE -> {
                printer.print("Double");
                if (t.isNullable()) {
                    printer.print("?");
                }
            }
            case FLOAT -> {
                printer.print("Float");
                if (t.isNullable()) {
                    printer.print("?");
                }
            }
            case INT -> {
                printer.print("Int");
                if (t.isNullable()) {
                    printer.print("?");
                }
            }
            case LONG -> {
                printer.print("Long");
                if (t.isNullable()) {
                    printer.print("?");
                }
            }
            case BOOLEAN -> {
                printer.print("Boolean");
                if (t.isNullable()) {
                    printer.print("?");
                }
            }
            case SHORT -> {
                printer.print("Short");
                if (t.isNullable()) {
                    printer.print("?");
                }
            }
            case BYTE -> {
                printer.print("Byte");
                if (t.isNullable()) {
                    printer.print("?");
                }
            }
        }

        return null;
    }

    @Override
    public Void visitWildcard(final WildcardType wildcardType,
                              final CodeContext context) {
        final var extendsBound = wildcardType.getExtendsBound();
        final var superBound = wildcardType.getSuperBound();

        if (extendsBound != null) {
            printer.print("out ");
            extendsBound.accept(this, context);
        } else if (superBound != null) {
            superBound.accept(this, context);
        } else {
            throw new UnsupportedOperationException("wildcard without any bound is not supported");
        }
        return null;
    }

    @Override
    public Void visitNoType(final NoType t, final CodeContext codeContext) {
        if (t instanceof UnitType) {
            printer.print("Unit");
        }
        return null;
    }

    //Statements
    @Override
    public Void visitIfStatement(final IfStatement ifStatement,
                                 final CodeContext context) {
        return super.visitIfStatement(ifStatement, context);
    }

    @Override
    protected boolean useSemiColonAfterStatement() {
        return false;
    }

    @Override
    protected Name resolveClassName(final Name className,
                                    final CodeContext context) {
        final var qualifiedName = QualifiedName.from(className);
        if ("kotlin".equals(qualifiedName.packageName().toString())) {
            return qualifiedName.simpleName();
        }
        return super.resolveClassName(className, context);
    }

    @Override
    protected Name name(final Name value) {
        if ("in".equals(value.toString())) {
            return Name.of("`" + value + "`");
        }
        return super.name(value);
    }

    @Override
    protected Name resolveAnnotationClassName(final AnnotationExpression annotation, final CodeContext context) {
        final var className = super.resolveAnnotationClassName(annotation, context);

        /*TODO
        final var annotationTarget = ((Attribute.Compound)annotation).getTarget();

        if (annotationTarget != null) {
            return Name.of(annotationTarget.getPrefix() + ":" + className);
        }
        */

        return className;
    }

    @Override
    public Void visit(final AnnotationValue av, final CodeContext codeContext) {
        throw new UnsupportedOperationException();
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
        printer.print("::class");
        return null;
    }

    @Override
    public Void visitUnknown(final AnnotationValue av, final CodeContext codeContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Void visit(final Element e, final CodeContext codeContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Void visitTypeParameter(final TypeParameterElement e, final CodeContext codeContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Void visitUnknown(final Element e, final CodeContext codeContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Void visit(final TypeMirror t, final CodeContext codeContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Void visitNull(final NullType t, final CodeContext codeContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Void visitTypeVariable(final TypeVariable t, final CodeContext codeContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Void visitExecutable(final ExecutableType t, final CodeContext codeContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Void visitUnknown(final TypeMirror t, final CodeContext codeContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Void visitUnion(final UnionType t, final CodeContext codeContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Void visitIntersection(final IntersectionType t, final CodeContext codeContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Void visitArray(final List<? extends AnnotationValue> array, final CodeContext param) {
        printer.print("[");

        final var lastIndex = array.size() - 1;

        for (int valueIndex = 0; valueIndex < array.size(); valueIndex++) {
            array.get(valueIndex).accept(this, param);
            if (valueIndex < lastIndex) {
                printer.print(", ");
            }
        }

        printer.print("]");

        return null;
    }

    @Override
    public Void visitVarType(final VarTypeImpl varType, final CodeContext codeContext) {
        throw new UnsupportedOperationException();
    }
    @Override
    public Void visitClassDeclaration(final ClassDeclaration classDeclaration, final CodeContext context) {
        printer.printIndent();

        final var annotations = classDeclaration.getAnnotations();

        if (annotations.size() > 0) {
            throw new UnsupportedOperationException();
        }

        printAnnotations(annotations, true, context);

        printModifiers(classDeclaration.getModifiers());

        if (!classDeclaration.getModifiers().isEmpty()) {
            printer.print(" ");
        }

        switch (classDeclaration.getKind()) {
            case CLASS -> printer.print("class ");
            case INTERFACE -> printer.print("interface ");
        }

        printer.print(classDeclaration.getSimpleName());

        final var primaryConstructor = classDeclaration.getPrimaryConstructor();

        if (primaryConstructor != null) {
            visitPrimaryConstructor(primaryConstructor, context);
        }

        final var extendsTypes = new ArrayList<Expression>();

        final var superType = classDeclaration.getExtending();

        if (superType != null) {
            extendsTypes.add(superType);
        }

        extendsTypes.addAll(classDeclaration.getImplementing());

        if (extendsTypes.size() > 0) {
            printer.print(" : ");

            for (int extendsIndex = 0; extendsIndex < extendsTypes.size(); extendsIndex++) {
                if (extendsIndex > 0) {
                    printer.print(", ");
                }
                final var extendsType = extendsTypes.get(extendsIndex);
                extendsType.accept(this, context);
            }
        }

        final var enclosedElements = classDeclaration.getEnclosed();

        if (enclosedElements.size() > 0) {
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
        }

        printer.deIndent();
        return null;
    }

    @Override
    public Void visitMethodDeclaration(final MethodDeclaration methodDeclaration,
                                       final CodeContext context) {
        if (methodDeclaration.getKind() == ElementKind.CONSTRUCTOR) {
            return visitSecondaryConstructor(methodDeclaration, context);
        } else {
            return visitMethod(methodDeclaration, context);
        }
    }

    private Void visitSecondaryConstructor(final MethodDeclaration methodDeclaration,
                                           final CodeContext context) {
        final var modifiers = methodDeclaration.getModifiers();
        printModifiers(modifiers);

        if (modifiers.size() > 0) {
            printer.print(" constructor");
            visitMethodParameters(methodDeclaration.getParameters(), context);
            printer.print(": ");
            methodDeclaration.getBody().ifPresent(body -> {
                final var statements = body.getStatements();
                if (!statements.isEmpty()) {
                    statements.get(0).accept(this, context);
                }
            });
        }
        return null;
    }

    private Void visitMethod(final MethodDeclaration methodDeclaration,
                             final CodeContext context) {
        final var annotations = methodDeclaration.getAnnotations();
        if (annotations.size() > 0) {
            //printAnnotations(annotations, true, context);
            //printer.print(" ");
            throw new UnsupportedOperationException();
        }

        final var modifiers = methodDeclaration.getModifiers();
        printModifiers(modifiers);

        if (modifiers.size() > 0) {
            printer.print(" ");
        }

        printer.printIndent();
        printer.print("fun ");

        printer.print(methodDeclaration.getSimpleName());

        visitMethodParameters(methodDeclaration.getParameters(), context);

        if (methodDeclaration.getKind() != ElementKind.CONSTRUCTOR) {
            final var returnType = methodDeclaration.getReturnType();

            if (returnType.getType().getKind() != TypeKind.VOID) {
                printer.print(" : ");
                returnType.accept(this, context);
                printer.print(" ");
            }
        }
        methodDeclaration.getBody().ifPresent(body -> body.accept(this, context));
        return null;
    }

    @Override
    public Void visitTypeParameter(final TypeParameter typeParameter, final CodeContext param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Void visitPrimitiveTypeExpression(final PrimitiveTypeExpression primitiveTypeExpression, final CodeContext param) {
        throw new UnsupportedOperationException();
    }
}
