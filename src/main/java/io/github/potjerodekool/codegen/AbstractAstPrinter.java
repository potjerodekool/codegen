package io.github.potjerodekool.codegen;

import io.github.potjerodekool.codegen.model.CompilationUnit;
import io.github.potjerodekool.codegen.model.CompilationUnitVisitor;
import io.github.potjerodekool.codegen.io.Printer;
import io.github.potjerodekool.codegen.model.element.*;
import io.github.potjerodekool.codegen.model.element.ElementVisitor;
import io.github.potjerodekool.codegen.model.symbol.PackageSymbol;
import io.github.potjerodekool.codegen.model.tree.MethodDeclaration;
import io.github.potjerodekool.codegen.model.tree.expression.*;
import io.github.potjerodekool.codegen.model.tree.type.AnnotatedTypeExpression;
import io.github.potjerodekool.codegen.model.tree.type.NoTypeExpression;
import io.github.potjerodekool.codegen.model.tree.type.ParameterizedType;
import io.github.potjerodekool.codegen.model.tree.type.VarTypeExpression;
import io.github.potjerodekool.codegen.model.tree.statement.VariableDeclaration;
import io.github.potjerodekool.codegen.model.symbol.ClassSymbol;
import io.github.potjerodekool.codegen.model.tree.*;
import io.github.potjerodekool.codegen.model.tree.statement.*;
import io.github.potjerodekool.codegen.model.type.*;
import io.github.potjerodekool.codegen.model.util.Elements;
import io.github.potjerodekool.codegen.model.util.QualifiedName;
import io.github.potjerodekool.codegen.model.util.type.Types;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public abstract class AbstractAstPrinter implements CompilationUnitVisitor<Void, CodeContext>,
        TreeVisitor<Void, CodeContext>,
        ElementVisitor<Void, CodeContext>,
        TypeVisitor<Void, CodeContext>,
        AnnotationValueVisitor<Void, CodeContext> {

    protected final Printer printer;
    private final Types types;

    protected AbstractAstPrinter(final Printer printer,
                                 final Types types) {
        this.printer = printer;
        this.types = types;
    }

    protected Types getTypes() {
        return types;
    }

    @Override
    public Void visitCompilationUnit(final CompilationUnit compilationUnit,
                                     final CodeContext context) {
        final var packageDeclaration = compilationUnit.getPackageDeclaration();
        final PackageSymbol packageElement;

        if (packageDeclaration != null) {
            packageElement = packageDeclaration.getPackageSymbol();
        } else {
            packageElement = compilationUnit.getPackageElement();
        }

        if (!packageElement.isDefaultPackage()) {
            packageElement.accept(this, context);
            printer.printLn();
            printer.printLn();
        }

        final var imports = compilationUnit.getImports();

        if (imports.size() > 0) {
            imports.forEach(importStr -> {
                printer.print("import " + importStr);
                if (useSemiColonAfterStatement()) {
                    printer.print(";");
                }
                printer.printLn();
            });
            printer.printLn();
        }

        compilationUnit.getDefinitions().stream()
                .filter(definition ->  !(definition instanceof PackageDeclaration))
                .forEach(type -> type.accept(this, context));
        return null;
    }

    @Override
    public Void visit(final Element e, final CodeContext codeContext) {
        return visitUnknown(e, codeContext);
    }

    //Elements
    @Override
    public Void visitPackage(final PackageElement packageElement,
                             final CodeContext context) {
        if (!packageElement.isUnnamed()) {
            printer.print("package " + packageElement.getQualifiedName());

            if (useSemiColonAfterStatement()) {
                printer.print(";");
            }
        }
        return null;
    }

    @Override
    public Void visitPackageDeclaration(final PackageDeclaration packageDeclaration, final CodeContext param) {
        final var packageElement = packageDeclaration.getPackageSymbol();

        if (!packageElement.isUnnamed()) {
            printer.print("package " + packageElement.getQualifiedName());

            if (useSemiColonAfterStatement()) {
                printer.print(";");
            }
        }
        return null;
    }

    @Override
    public Void visitType(final TypeElement e, final CodeContext codeContext) {
        return visitUnknown(e, codeContext);
    }

    protected void printModifiers(final Set<Modifier> modifiers) {
        if (modifiers.size() > 0) {
            final var mods = modifiers.stream()
                    .map(this::modifierToString)
                    .collect(Collectors.joining(" "));
            printer.print(mods);
        }
    }

    private String modifierToString(final Modifier modifier) {
        return modifier.name().toLowerCase();
    }

    @Override
    public Void visitExecutable(final ExecutableElement methodElement, final CodeContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Void visitTypeParameter(final TypeParameterElement e, final CodeContext codeContext) {
        return visitUnknown(e, codeContext);
    }

    @Override
    public Void visitUnknown(final Element e, final CodeContext codeContext) {
        throw new UnsupportedOperationException();
    }

    public void visitMethodParameters(final List<VariableDeclaration> parameters,
                                      final CodeContext context) {
        printer.print("(");

        final int lastParameter = parameters.size() - 1;

        for (int i = 0; i < parameters.size(); i++) {
            final var parameter = parameters.get(i);
            parameter.accept(this, context);
            if (i < lastParameter) {
                printer.print(", ");
            }
        }
        printer.print(")");
    }

    /*
    @Override
    public Void visitVariable(final VariableElement variableElement,
                              final CodeContext context) {
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
            printer.printLn(";");
        }
        return null;
    }
     */

    protected void printAnnotations(final List<AnnotationExpression> annotations,
                                    final boolean addNewLineAfterAnnotation,
                                    final CodeContext context) {
        if (annotations.isEmpty()) {
            return;
        }

        final var lastIndex = annotations.size() - 1;

        for (int i = 0; i < annotations.size(); i++) {
            if (addNewLineAfterAnnotation) {
                printer.printIndent();
            }

            final var annotation = annotations.get(i);
            printAnnotation(annotation, addNewLineAfterAnnotation, context);

            if (i < lastIndex && !addNewLineAfterAnnotation) {
                printer.print(" ");
            }
        }
    }

    protected void printAnnotation(final AnnotationExpression annotation,
                                   final boolean addNewLineAfterAnnotation,
                                   final CodeContext context) {
        final Name annotationName = resolveAnnotationClassName(annotation, context);

        printer.print("@").print(annotationName).print("(");

        final var elementValues = annotation.getArguments();
        final var lastIndex = elementValues.size() - 1;
        final var childContext = context.child(annotation);
        final var elementValueIndex = new AtomicInteger();

        annotation.getArguments().forEach((key, value) -> {
            printer.print(name(key)).print(" = ");
            value.accept(this, childContext);

            if (elementValueIndex.get() < lastIndex) {
                printer.print(", ");
            }
            elementValueIndex.incrementAndGet();
        });

        printer.print(")");

        if (addNewLineAfterAnnotation) {
            printer.printLn();
        }
    }

    protected Name resolveAnnotationClassName(final AnnotationExpression annotation,
                                              final CodeContext context) {
        final var annotType = (ParameterizedType) annotation.getAnnotationType();
        final var name = ((NameExpression) annotType.getClazz()).getName();

        return resolveClassName(
                name,
                context
        );
    }

    protected Name name(final String value) {
        return name(Name.of(value));
    }

    protected Name name(final Name value) {
        return value;
    }

    //Statements
    @Override
    public Void visitBlockStatement(final BlockStatement blockStatement,
                                    final CodeContext context) {
        printer.printLn("{");
        printer.indent();

        final var statements = blockStatement.getStatements();

        statements.forEach(statement -> {
            printer.printIndent();
            statement.accept(this, context);
            if (useSemiColonAfterStatement()) {
                printer.print(";");
            }
            printer.printLn();
        });
        printer.deIndent();

        if (statements.isEmpty()) {
            printer.printLn();
        }
        printer.printIndent();
        printer.printLn("}");
        return null;
    }

    @Override
    public Void visitExpressionStatement(final ExpressionStatement expressionStatement,
                                         final CodeContext context) {
        expressionStatement.getExpression().accept(this, context);
        return null;
    }

    @Override
    public Void visitReturnStatement(final ReturnStatement returnStatement,
                                     final CodeContext context) {
        printer.print("return ");
        returnStatement.getExpression().accept(this, context);
        return null;
    }

    @Override
    public Void visitUnknown(final Statement statement,
                             final CodeContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Void visitIfStatement(final IfStatement ifStatement,
                                 final CodeContext context) {
        printer.print("if (");
        ifStatement.getCondition().accept(this, context);
        printer.print(")");
        ifStatement.getBody().accept(this, context);
        return null;
    }

    //Expressions
    @Override
    public Void visitBinaryExpression(final BinaryExpression binaryExpression,
                                      final CodeContext context) {
        binaryExpression.getLeft().accept(this, context);
        printer.print(" ");

        switch (binaryExpression.getOperator()) {
            case ASSIGN -> printer.print("=");
            case MINUS -> printer.print("-");
            case NOT_EQUALS -> printer.print("!=");
        }

        printer.print(" ");
        binaryExpression.getRight().accept(this, context);
        return null;
    }

    @Override
    public Void visitFieldAccessExpression(final FieldAccessExpression fieldAccessExpression,
                                           final CodeContext context) {
        fieldAccessExpression.getScope().accept(this, context);
        printer.print(".");
        fieldAccessExpression.getField().accept(this, context);
        return null;
    }

    @Override
    public Void visitNameExpression(final NameExpression nameExpression,
                                    final CodeContext context) {
        final var symbol = nameExpression.getSymbol();

        if (symbol instanceof ClassSymbol classSymbol) {
            if (context.resolveCompilationUnit()
                    .filter(cu -> cu.getImports().contains(classSymbol.getQualifiedName()))
                    .isPresent()) {
                printer.print(classSymbol.getSimpleName());
            } else {
                printer.print(nameExpression.getName());
            }
        } else {
            printer.print(nameExpression.getName());
        }
        return null;
    }

    @Override
    public Void visitUnknown(final Expression expression,
                             final CodeContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Void visitArrayAccessExpression(final ArrayAccessExpression arrayAccessExpression,
                                           final CodeContext context) {
        arrayAccessExpression.getArrayExpression().accept(this, context);
        printer.print("[");
        arrayAccessExpression.getIndexExpression().accept(this, context);
        printer.print("]");
        return null;
    }

    @Override
    public Void visitLiteralExpression(final LiteralExpression literalExpression,
                                       final CodeContext context) {
        switch (literalExpression.getLiteralType()) {
            case NULL -> printer.print("null");
            case CLASS -> {
                final var classLiteralExpression = (ClassLiteralExpression) literalExpression;
                final var type = classLiteralExpression.getType();

                if (context.getAstNode() instanceof AnnotationMirror) {
                    final Name className;
                    if (type instanceof DeclaredType declaredType) {
                        className = resolveClassName(declaredType.asElement(), context);
                    } else {
                        final var boxedElement = types.boxedClass((PrimitiveType) type);
                        className = resolveClassName(boxedElement.getQualifiedName(), context);
                    }
                    printer.print(className + ".class");
                } else if (type instanceof DeclaredType declaredType) {
                    final Name className = resolveClassName(declaredType.asElement(), context);
                    printer.print(className + ".class");
                } else if (type instanceof PrimitiveType p) {
                    final var boxedElement = types.boxedClass(p);
                    final Name className = resolveClassName(boxedElement.getQualifiedName(), context);
                    printer.print(className + ".TYPE");
                } else {
                    throw new UnsupportedOperationException("TODO");
                }
            }
            case STRING -> {
                final var le = (StringValueLiteralExpression) literalExpression;
                printer.print("\"" + le.getValue() + "\"");
            }
            case CHAR -> {
                final var le = (StringValueLiteralExpression) literalExpression;
                printer.print("'" + le.getValue() + "'");
            }
            default -> {
                final var le = (StringValueLiteralExpression) literalExpression;
                printer.print(le.getValue());
            }
        }
        return null;
    }

    @Override
    public Void visitMethodCall(final MethodCallExpression methodCallExpression,
                                final CodeContext context) {
        methodCallExpression.getTarget()
                .ifPresent(target -> {
                    target.accept(this, context);
                    printer.print(".");
                });

        printer.print(methodCallExpression.getMethodName());
        printer.print("(");

        final var arguments = methodCallExpression.getArguments();
        final var lastIndex = arguments.size() - 1;

        for (int i = 0; i < arguments.size(); i++) {
            arguments.get(i).accept(this, context);
            if (i < lastIndex) {
                printer.print(", ");
            }
        }

        printer.print(")");
        return null;
    }

    @Override
    public Void visitVariableDeclaration(final VariableDeclaration variableDeclaration,
                                         final CodeContext context) {
        final var isField = variableDeclaration.getSymbol().getKind() == ElementKind.FIELD;
        final var annotations = variableDeclaration.getAnnotations();

        printAnnotations(annotations, isField, context);

        final var modifiers = variableDeclaration.getModifiers();

        if (modifiers.size() > 0
                && isField) {
            printer.printIndent();
        }

        if (modifiers.size() > 0
                && annotations.size() > 0
                && variableDeclaration.getSymbol().getKind() == ElementKind.PARAMETER) {
            printer.print(" ");
        }

        printModifiers(modifiers);

        if (annotations.size() > 0
                || modifiers.size() > 0) {
            printer.print(" ");
        }

        variableDeclaration.getSymbol().asType().accept(this, context);
        printer.print(" ");
        printer.print(variableDeclaration.getName());

        if (isField) {
            printer.printLn(";");
        }
        return null;
    }

    @Override
    public Void visitNewClassExpression(final NewClassExpression newClassExpression, final CodeContext codeContext) {
        return visitUnknown(newClassExpression, codeContext);
    }

    @Override
    public Void visitArrayInitializerExpression(final ArrayInitializerExpression arrayInitializerExpression,
                                                final CodeContext context) {
        return visitUnknown(arrayInitializerExpression, context);
    }

    @Override
    public Void visitNamedMethodArgumentExpression(final NamedMethodArgumentExpression namedMethodArgumentExpression, final CodeContext param) {
        return visitUnknown(namedMethodArgumentExpression, param);
    }

    //Types

    @Override
    public Void visitNoType(final NoType t, final CodeContext codeContext) {
        if (t.getKind() == TypeKind.VOID) {
            printer.print("void");
        }
        return null;
    }

    @Override
    public Void visitNoType(final NoTypeExpression noTypeExpression,
                            final CodeContext codeContext) {
        noTypeExpression.getType().accept(this, codeContext);
        return null;
    }

    @Override
    public Void visitUnknown(final TypeMirror t, final CodeContext codeContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Void visitUnion(final UnionType t, final CodeContext codeContext) {
        return visitUnknown(t, codeContext);
    }

    @Override
    public Void visitIntersection(final IntersectionType t,
                                  final CodeContext codeContext) {
        return visitUnknown(t, codeContext);
    }

    @Override
    public Void visitDeclared(final DeclaredType declaredType,
                              final CodeContext context) {
        /*TODO
        final var annotations = declaredType.getAnnotationMirrors();

        if (annotations.size() > 0) {
            printAnnotations(annotations,false, context);
            printer.print(" ");
        }
        */

        printer.print(resolveClassName(declaredType.asElement(), context));

        final var typeArgs = declaredType.getTypeArguments();

        if (typeArgs.size() > 0) {
            printer.print("<");

            final int lastIndex = typeArgs.size() - 1;

            for (int i = 0; i < typeArgs.size(); i++) {
                final var typeArg = typeArgs.get(i);
                typeArg.accept(this, context);

                if (i < lastIndex) {
                    printer.print(",");
                }
            }

            printer.print(">");
        }

        return null;
    }

    @Override
    public Void visitError(final ErrorType t,
                           final CodeContext codeContext) {
        return visitDeclared(t, codeContext);
    }

    @Override
    public Void visitTypeVariable(final TypeVariable t, final CodeContext codeContext) {
        return visitUnknown(t, codeContext);
    }

    @Override
    public Void visitWildcard(final WildcardType t, final CodeContext codeContext) {
        return visitUnknown(t, codeContext);
    }

    @Override
    public Void visitExecutable(final ExecutableType t, final CodeContext codeContext) {
        return visitUnknown(t, codeContext);
    }

    @Override
    public Void visit(final TypeMirror t, final CodeContext codeContext) {
        return visitUnknown(t, codeContext);
    }

    @Override
    public Void visitPrimitive(final PrimitiveType t, final CodeContext codeContext) {
        return visitUnknown(t, codeContext);
    }

    @Override
    public Void visitNull(final NullType t, final CodeContext codeContext) {
        return visitUnknown(t, codeContext);
    }

    @Override
    public Void visitArray(final ArrayType t, final CodeContext codeContext) {
        return visitUnknown(t, codeContext);
    }

    @Override
    public Void visit(final AnnotationValue av, final CodeContext codeContext) {
        return visitUnknown(av, codeContext);
    }

    @Override
    public Void visitBoolean(final boolean value, final CodeContext param) {
        printer.print(Boolean.toString(value));
        return null;
    }

    @Override
    public Void visitByte(final byte b, final CodeContext codeContext) {
        printer.print(Byte.toString(b));
        return null;
    }

    @Override
    public Void visitChar(final char value, final CodeContext param) {
        printer.print(Character.toString(value));
        return null;
    }

    @Override
    public Void visitShort(final short value, final CodeContext param) {
        printer.print(Short.toString(value));
        return null;
    }

    @Override
    public Void visitInt(final int value, final CodeContext param) {
        printer.print(Integer.toString(value));
        return null;
    }

    @Override
    public Void visitLong(final long value, final CodeContext param) {
        printer.print(Long.toString(value));
        return null;
    }

    @Override
    public Void visitFloat(final float value, final CodeContext param) {
        printer.print(Float.toString(value));
        return null;
    }

    @Override
    public Void visitDouble(final double value, final CodeContext param) {
        printer.print(Double.toString(value));
        return null;
    }

    @Override
    public Void visitString(final String value, final CodeContext param) {
        printer.print("\"");
        printer.print(value);
        printer.print("\"");
        return null;
    }

    @Override
    public Void visitUnknown(final AnnotationValue av, final CodeContext codeContext) {
        throw new UnsupportedOperationException();
    }

    protected boolean isInImport(final Name className, final CodeContext codeContext) {
        if (className.toString().startsWith("java.lang.")) {
            return true;
        } else {
            final var compilationUnitOptional = codeContext.resolveCompilationUnit();
            if (compilationUnitOptional.isPresent()) {
                final var compilationUnit = compilationUnitOptional.get();
                return compilationUnit.getImports().contains(className);
            }
        }

        return false;
    }

    @Override
    public Void visitEnumConstant(final VariableElement enumValue, final CodeContext codeContext) {
        final var enumElement = (ClassSymbol) enumValue.getEnclosingElement();
        final var compilationUnitOptional = codeContext.resolveCompilationUnit();
        final Name enumClassName;

        if (compilationUnitOptional.isPresent()) {
            final var compilationUnit = compilationUnitOptional.get();
            if (compilationUnit.getImports().contains(enumElement.getQualifiedName())) {
                enumClassName = enumElement.getSimpleName();
            } else {
                enumClassName = enumElement.getQualifiedName();
            }
        } else {
            enumClassName = enumElement.getQualifiedName();
        }

        printer
                .print(enumClassName)
                .print(".")
                .print(enumValue.getSimpleName());

        return null;
    }

    @Override
    public Void visitAnnotation(final AnnotationMirror a, final CodeContext codeContext) {
        printer.print("print unknown annotation " + a);
        return null;
    }

    protected boolean useSemiColonAfterStatement() {
        return true;
    }

    protected Name resolveClassName(final Element element,
                                    final CodeContext context) {
        return resolveClassName(Elements.getQualifiedName(element), context);
    }

    protected Name resolveClassName(final String className,
                                    final CodeContext context) {
        return resolveClassName(Name.of(className), context);
    }

    protected Name resolveClassName(final Name className,
                                    final CodeContext context) {
        final var compilationUnit = findCompilationUnit(context);

        if (compilationUnit.getImports().contains(className)) {
            return QualifiedName.from(className).simpleName();
        }

        return className;
    }

    private CompilationUnit findCompilationUnit(final CodeContext context) {
        final var astNode = context.getAstNode();
        if (astNode instanceof CompilationUnit cu) {
            return cu;
        } else {
            final var parentContext = context.getParentContext();

            if (parentContext != null) {
                return findCompilationUnit(parentContext);
            } else {
                throw new IllegalStateException("Failed to find compilation unit in context");
            }
        }
    }

    @Override
    public Void visitParameterizedType(final ParameterizedType parameterizedType, final CodeContext param) {
        parameterizedType.getClazz().accept(this, param);

        final var arguments = parameterizedType.getArguments();

        printer.print("<");
        final var lastIndex = arguments.size() -1;

        for (int i = 0; i < arguments.size(); i++) {
            arguments.get(i).accept(this, param);

            if (i < lastIndex) {
                printer.print(",");
            }
        }

        printer.print(">");
        return null;
    }

    @Override
    public Void visitAnnotatedType(final AnnotatedTypeExpression annotatedTypeExpression, final CodeContext context) {
        final var annotations = annotatedTypeExpression.getAnnotations();

        if (annotations.size() > 0) {
            throw new UnsupportedOperationException("TODO");
            //printAnnotations(annotations,false, context);
            //printer.print(" ");
        }

        final var element = types.asElement(annotatedTypeExpression.getIdentifier().getType());
        printer.print(resolveClassName(element, context));

        /*
        final var typeArgs = annotatedTypeExpression.getTypeArguments();

        if (typeArgs.size() > 0) {
            printer.print("<");

            final int lastIndex = typeArgs.size() - 1;

            for (int i = 0; i < typeArgs.size(); i++) {
                final var typeArg = typeArgs.get(i);
                typeArg.accept(this, context);

                if (i < lastIndex) {
                    printer.print(",");
                }
            }

            printer.print(">");
        }
         */

        return null;
    }
    @Override
    public Void visitMethodDeclaration(final MethodDeclaration methodDeclaration,
                                       final CodeContext context) {
        printer.printIndent();

        final var modifiers = methodDeclaration.getModifiers();
        printModifiers(modifiers);

        if (modifiers.size() > 0) {
            printer.print(" ");
        }

        if (methodDeclaration.getKind() != ElementKind.CONSTRUCTOR) {
            methodDeclaration.getReturnType().accept(this, context);
            printer.print(" ");
        }

        printer.print(methodDeclaration.getSimpleName());

        visitMethodParameters(methodDeclaration.getParameters(), context);
        printer.printLn();

        methodDeclaration.getBody().ifPresent(body -> body.accept(this, context));
        return null;
    }

    @Override
    public Void visitVarTypeExpression(final VarTypeExpression varTypeExpression, final CodeContext param) {
        printer.print("var");
        return null;
    }
}

