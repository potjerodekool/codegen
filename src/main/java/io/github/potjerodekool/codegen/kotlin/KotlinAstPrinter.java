package io.github.potjerodekool.codegen.kotlin;

import io.github.potjerodekool.codegen.AbstractAstPrinter;
import io.github.potjerodekool.codegen.CodeContext;
import io.github.potjerodekool.codegen.model.tree.*;
import io.github.potjerodekool.codegen.model.tree.expression.*;
import io.github.potjerodekool.codegen.io.Printer;
import io.github.potjerodekool.codegen.model.element.*;
import io.github.potjerodekool.codegen.model.tree.kotlin.KAnnotationExpression;
import io.github.potjerodekool.codegen.model.tree.kotlin.KMethodDeclaration;
import io.github.potjerodekool.codegen.model.tree.statement.ClassDeclaration;
import io.github.potjerodekool.codegen.model.tree.statement.VariableDeclaration;
import io.github.potjerodekool.codegen.model.tree.type.*;
import io.github.potjerodekool.codegen.model.tree.statement.IfStatement;
import io.github.potjerodekool.codegen.model.type.*;
import io.github.potjerodekool.codegen.model.type.immutable.WildcardType;
import io.github.potjerodekool.codegen.model.type.java.JavaArrayType;
import io.github.potjerodekool.codegen.model.type.kotlin.UnitType;
import io.github.potjerodekool.codegen.model.util.Counter;
import io.github.potjerodekool.codegen.model.util.Elements;
import io.github.potjerodekool.codegen.model.util.QualifiedName;
import io.github.potjerodekool.codegen.model.util.type.Types;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static io.github.potjerodekool.codegen.CollectionUtils.forEachWithIndexed;

public class KotlinAstPrinter extends AbstractAstPrinter
        implements TreeVisitor<Void, CodeContext> {

    public KotlinAstPrinter(final Printer printer,
                            final Types types) {
        super(printer, types);
    }

    void visitPrimaryConstructor(final MethodDeclaration<?> method,
                                 final CodeContext context) {
        if (!method.getAnnotations().isEmpty()) {
            printer.print(" ");
            printAnnotations(
                    method.getAnnotations(),
                    false,
                    false,
                    context
            );
            printer.print(" constructor");
        }
        visitMethodParameters(method.getParameters(), context);
    }

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
    public Void visitVariableDeclaration(final VariableDeclaration<?> variableDeclaration,
                                         final CodeContext context) {
        final var isField = variableDeclaration.getKind() == ElementKind.FIELD;
        final var modifiers = variableDeclaration.getModifiers();

        if (isField) {
            printer.printIndent();
        }

        if (!modifiers.isEmpty()) {
            printModifiers(modifiers);
            printer.print(" ");
        }

        printer.print(variableDeclaration.getName());

        final var varType = variableDeclaration.getVarType();

        if (varType != null
                && !(varType instanceof VarTypeExpression)) {
            printer.print(" : ");
            varType.accept(this,context);
        }

        variableDeclaration.getInitExpression().ifPresent(initExpression -> {
            printer.print(" = ");
            initExpression.accept(this, context);
        });
        return null;
    }


    @Override
    public Void visitNewClassExpression(final NewClassExpression newClassExpression,
                                        final CodeContext context) {
        newClassExpression.getClazz().accept(this, context);
        printer.print("(");
        printExpressionList(newClassExpression.getArguments(), ", ", context);
        printer.print(")");
        return null;
    }

    private String resolveArrayOfMethodName(final ArrayInitializerExpression arrayInitializerExpression) {
        final String arrayOfMethodName;

        final var literalTypeOptional = detectTypeOfValues(arrayInitializerExpression);

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

        return arrayOfMethodName;
    }

    @Override
    public Void visitArrayInitializerExpression(final ArrayInitializerExpression arrayInitializerExpression,
                                                final CodeContext context) {
        final String arrayOfMethodName = resolveArrayOfMethodName(arrayInitializerExpression);

        printer.print(arrayOfMethodName);
        printer.print("(");

        final var values = arrayInitializerExpression.getValues();

        final var lastIndex = values.size() -1;

        final var childContext = context.child(arrayInitializerExpression);

        forEachWithIndexed(values, (element, index) -> {
            element.accept(this, childContext);
            if (index < lastIndex) {
                printer.print(", ");
            }
        });

        printer.print(")");
        return null;
    }

    private Optional<LiteralType> detectTypeOfValues(final ArrayInitializerExpression arrayInitializerExpression) {
        final var values = arrayInitializerExpression.getValues();

        if (!values.isEmpty()) {
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
    public Void visitAnnotationExpression(final AnnotationExpression annotationExpression,
                                          final CodeContext context) {
        final var elementValues = annotationExpression.getArguments();
        final var annotationType = (DeclaredType) annotationExpression.getAnnotationType().getType();

        if (!isPartOfAnnotationExpressionOrArrayInitializerExpression(context)) {
            printer.print("@");
        }

        final var className = resolveAnnotationClassName(annotationType.asElement(), context);

        if (annotationExpression instanceof KAnnotationExpression kAnnotationExpression) {
            final var target = kAnnotationExpression.getTarget();

            if (target != null) {
                if (target == KAnnotationExpression.Target.FIELD) {
                    printer.print("field:");
                }
            }
        }

        printer.print(className);

        if (!elementValues.isEmpty()) {
            printer.print("(");

            final var lastIndex = elementValues.size() - 1;
            final var counter = new Counter();

            final var childContext = context.child(annotationExpression);

            elementValues.forEach((name,value) -> {
                printer.print(name);
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

        if (!elementValues.isEmpty()) {
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
    public Void visitWildCardTypeExpression(final WildCardTypeExpression wildCardTypeExpression,
                                            final CodeContext context) {
        final var bound = wildCardTypeExpression.getTypeExpression();

        if (wildCardTypeExpression.getBoundKind() == BoundKind.EXTENDS) {
            printer.print("out ");
            bound.accept(this, context);
        } else if (wildCardTypeExpression.getBoundKind() == BoundKind.SUPER) {
            bound.accept(this, context);
        } else {
            throw new UnsupportedOperationException("wildcard without any bound is not supported");
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

        if (qualifiedName.toString().startsWith("kotlin.")) {
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

        if (annotation instanceof KAnnotationExpression kAnnotationExpression) {
            final var target = kAnnotationExpression.getTarget();

            if (target == KAnnotationExpression.Target.FIELD) {
                return Name.of("field:" + className);
            }
        }

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

        forEachWithIndexed(array, (element, index) -> {
            element.accept(this, param);
            if (index < lastIndex) {
                printer.print(", ");
            }
        });

        printer.print("]");

        return null;
    }

    @Override
    public Void visitVarType(final VarType varType, final CodeContext codeContext) {
        throw new UnsupportedOperationException();
    }
    @Override
    public Void visitClassDeclaration(final ClassDeclaration<?> classDeclaration, final CodeContext context) {
        final var classContext = context.child(classDeclaration);

        printer.printIndent();

        final var annotations = classDeclaration.getAnnotations();

        printAnnotations(annotations, true, false, classContext);

        printModifiers(classDeclaration.getModifiers());

        if (!classDeclaration.getModifiers().isEmpty()) {
            printer.print(" ");
        }

        final var elementKind = classDeclaration.getKind();

        if (elementKind == ElementKind.CLASS) {
            printer.print("class ");
        } else if (elementKind == ElementKind.INTERFACE) {
            printer.print("interface ");
        } else if (elementKind == ElementKind.OBJECT) {
            printer.print("object ");
        }

        printer.print(classDeclaration.getSimpleName());

        final var primaryConstructor = classDeclaration.getPrimaryConstructor();

        if (primaryConstructor != null) {
            visitPrimaryConstructor(primaryConstructor, classContext);
        }

        final var extendsTypes = new ArrayList<Expression>();

        final var superType = classDeclaration.getExtending();

        if (superType != null) {
            extendsTypes.add(superType);
        }

        extendsTypes.addAll(classDeclaration.getImplementing());

        if (!extendsTypes.isEmpty()) {
            printer.print(" : ");

            final var lastIndex = extendsTypes.size() - 1;

            forEachWithIndexed(extendsTypes, (extendsType, index) -> {
                extendsType.accept(this, classContext);
                if (index < lastIndex) {
                    printer.print(", ");
                }
            });
        }

        final var enclosedElements = classDeclaration.getEnclosed();

        if (!enclosedElements.isEmpty()) {
            printer.printLn(" {");
            printer.indent();

            printer.printLn();
            final var lastIndex = enclosedElements.size() - 1;

            forEachWithIndexed(enclosedElements, (enclosedElement, index) -> {
                enclosedElement.accept(this, classContext);

                if (index < lastIndex) {
                    printer.printLn();
                    printer.printLn();
                }
            });

            printer.deIndent();

            printer.printLn("}");
        }

        printer.deIndent();
        return null;
    }

    @Override
    public Void visitMethodDeclaration(final MethodDeclaration<?> methodDeclaration,
                                       final CodeContext context) {
        final var kMethodDeclaration = (KMethodDeclaration) methodDeclaration;

        if (kMethodDeclaration.getKind() == ElementKind.CONSTRUCTOR) {
            return visitSecondaryConstructor(kMethodDeclaration, context);
        } else {
            return visitMethod(kMethodDeclaration, context);
        }
    }

    private Void visitSecondaryConstructor(final MethodDeclaration<?> methodDeclaration,
                                           final CodeContext context) {
        final var modifiers = methodDeclaration.getModifiers();
        printModifiers(modifiers);

        if (!modifiers.isEmpty()) {
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

    private Void visitMethod(final KMethodDeclaration methodDeclaration,
                             final CodeContext context) {
        printer.printIndent();

        final var annotations = methodDeclaration.getAnnotations();
        if (!annotations.isEmpty()) {
            printAnnotations(annotations, true, true, context);
            printer.print(" ");
        }

        final var modifiers = methodDeclaration.getModifiers();
        printModifiers(modifiers);

        if (!modifiers.isEmpty()) {
            printer.print(" ");
        }

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

        if (methodDeclaration.getBody().isEmpty()) {
            printer.printLn();
        }

        return null;
    }

    @Override
    public Void visitTypeParameter(final TypeParameter typeParameter, final CodeContext param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Void visitPrimitiveTypeExpression(final PrimitiveTypeExpression primitiveTypeExpression, final CodeContext param) {
        return primitiveTypeExpression.getType().accept(this, param);
    }

    @Override
    public Void visitClassOrInterfaceTypeExpression(final ClassOrInterfaceTypeExpression classOrInterfaceTypeExpression,
                                                    final CodeContext context) {
        final var type = classOrInterfaceTypeExpression.getType();

        boolean printTypArgs = true;

        if (type instanceof ClassType classType) {
            classType.accept(this, context);
            printTypArgs = false;
        } else {
            printer.print(classOrInterfaceTypeExpression.getName());
        }

        if (printTypArgs) {
            final var arguments = classOrInterfaceTypeExpression.getTypeArguments();

            if (!arguments.isEmpty()) {
                printer.print("<");
                final var lastIndex = arguments.size() -1;

                forEachWithIndexed(arguments, (arg, index) -> {
                    arg.accept(this, context);

                    if (index < lastIndex) {
                        printer.print(",");
                    }
                });

                printer.print(">");
            }

            if (classOrInterfaceTypeExpression.isNullable()) {
                printer.print("?");
            }
        }

        return null;
    }
}
