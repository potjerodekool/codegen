package io.github.potjerodekool.codegen.kotlin;

import io.github.potjerodekool.codegen.*;
import io.github.potjerodekool.codegen.extension.buildin.BuildInDefaultValueResolver;
import io.github.potjerodekool.codegen.model.Attribute;
import io.github.potjerodekool.codegen.model.CompilationUnit;
import io.github.potjerodekool.codegen.model.element.*;
import io.github.potjerodekool.codegen.model.symbol.AbstractSymbol;
import io.github.potjerodekool.codegen.model.symbol.ClassSymbol;
import io.github.potjerodekool.codegen.model.symbol.MethodSymbol;
import io.github.potjerodekool.codegen.model.symbol.kotlin.KClassSymbolBuilder;
import io.github.potjerodekool.codegen.model.tree.*;
import io.github.potjerodekool.codegen.model.tree.expression.*;
import io.github.potjerodekool.codegen.model.tree.type.*;
import io.github.potjerodekool.codegen.model.tree.statement.*;
import io.github.potjerodekool.codegen.model.type.*;
import io.github.potjerodekool.codegen.model.type.immutable.WildcardType;
import io.github.potjerodekool.codegen.model.type.kotlin.UnitType;
import io.github.potjerodekool.codegen.model.util.Elements;
import io.github.potjerodekool.codegen.model.util.StringUtils;
import io.github.potjerodekool.codegen.model.util.type.Types;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static io.github.potjerodekool.codegen.model.util.StringUtils.firstUpper;

public class JavaToKotlinConverter implements
        TreeVisitor<Object, CodeContext>,
        TypeVisitor<TypeMirror, CodeContext>,
        AnnotationValueVisitor<Object, CodeContext> {

    private static final Map<String, String> JAVA_TO_KOTLIN_TYPE = Map.ofEntries(
            Map.entry("java.lang.String", KotlinClasses.KOTLIN_STRING),
            Map.entry("java.lang.Integer", KotlinClasses.KOTLIN_INT),
            Map.entry("java.lang.Boolean", KotlinClasses.KOTLIN_BOOLEAN),
            Map.entry("java.lang.Byte", KotlinClasses.KOTLIN_BYTE),
            Map.entry("java.lang.Short", KotlinClasses.KOTLIN_SHORT),
            Map.entry("java.lang.Character", KotlinClasses.KOTLIN_CHAR),
            Map.entry("java.lang.Float", KotlinClasses.KOTLIN_FLOAT),
            Map.entry("java.lang.Double", KotlinClasses.KOTLIN_DOUBLE),
            Map.entry("java.lang.Long", KotlinClasses.KOTLIN_LONG),
            Map.entry("java.lang.Object", "kotlin.Any"),
            Map.entry("java.util.List", "kotlin.collections.MutableList"),
            Map.entry("java.util.Map", "kotlin.collections.MutableMap")
    );

    private final Elements elements;
    private final Types types;
    private final BuildInDefaultValueResolver defaultValueResolver = new BuildInDefaultValueResolver();

    public JavaToKotlinConverter(final Elements elements,
                                 final Types types) {
        this.elements = elements;
        this.types = types;
    }

    public CompilationUnit convert(final CompilationUnit compilationUnit) {
        if (compilationUnit.getLanguage() == Language.KOTLIN) {
            return compilationUnit;
        }
        final var newCompilationUnit = new CompilationUnit(Language.KOTLIN);
        final var packageDeclaration = compilationUnit.getPackageDeclaration();

        if (packageDeclaration != null) {
            newCompilationUnit.packageDeclaration(packageDeclaration);
        }

        final var codeContext = new CodeContext(newCompilationUnit);

        final var definitions = compilationUnit.getDefinitions().stream()
                .filter(definition -> !(definition instanceof PackageDeclaration))
                        .toList();
        definitions.forEach(compilationUnit::remove);

        final var newDefinitions = definitions.stream()
                .map(definition -> (Tree) definition.accept(this, codeContext))
                .toList();

        newDefinitions.forEach(definition -> {
            if (definition instanceof ClassDeclaration classDeclaration) {
                newCompilationUnit.classDeclaration(classDeclaration);
            } else if (definition instanceof MethodDeclaration methodDeclaration) {
                newCompilationUnit.methodDeclaration(methodDeclaration);
            }
        });


        return newCompilationUnit;
    }

    @Override
    public Object visitPackageDeclaration(final PackageDeclaration packageDeclaration, final CodeContext param) {
        return packageDeclaration;
    }

    //Elements

    @Override
    public Object visitMethodDeclaration(final MethodDeclaration jMethodDeclaration,
                                         final CodeContext context) {
        final var childContext = context.child(jMethodDeclaration);

        final var newParameters = jMethodDeclaration.getParameters().stream()
                .map(it -> (VariableDeclaration) it.accept(this, context))
                .toList();

        newParameters.forEach(parameter -> {
            parameter.removeModifier(Modifier.VAL);
            parameter.removeModifier(Modifier.VAR);
        });

        final var newMethod = new MethodDeclaration()
                .simpleName(jMethodDeclaration.getSimpleName())
                .kind(jMethodDeclaration.getKind())
                .returnType((Expression) jMethodDeclaration.getReturnType().accept(this, context))
                .typeParameters(jMethodDeclaration.getTypeParameters());

        final var newAnnotations = jMethodDeclaration.getAnnotations().stream()
                .map(annotationExpression -> (AnnotationExpression) annotationExpression.accept(this, context))
                .toList();
        newAnnotations.forEach(newMethod::annotation);

        jMethodDeclaration.getBody().ifPresent(body -> {
            final var newBody = (BlockStatement) body.accept(this, childContext);
            newMethod.body(newBody);
        });

        newMethod.modifiers(convertModifiers(jMethodDeclaration.getModifiers(),
                it -> !(it == Modifier.DEFAULT || it == Modifier.STATIC)));

        final var overrideAnnotation = newMethod.getAnnotation("java.lang.Override");

        if (overrideAnnotation != null) {
            newMethod.removeAnnotation(overrideAnnotation);
            newMethod.modifiers(Modifier.OVERRIDE);
        }

        if (jMethodDeclaration.getModifiers().contains(Modifier.STATIC)) {
            newMethod.annotation("kotlin.jvm.JvmStatic");
        }

        return newMethod;
    }

    private boolean isUtilityClass(final ClassDeclaration classDeclaration) {
        return classDeclaration.getEnclosed().stream()
                .filter(element -> !isElementOfKind(element, ElementKind.CONSTRUCTOR))
                .allMatch(element ->
                        isElementOfKind(element, ElementKind.METHOD)
                        && ((MethodDeclaration) element).hasModifier(Modifier.STATIC)
                );
    }

    private boolean isElementOfKind(final Tree tree,
                                    final ElementKind kind) {
        return tree instanceof ElementTree elementTree
                && elementTree.getKind() == kind;
    }

    private void conditionalAddPrimaryConstructor(final ClassDeclaration kClassDeclaration) {
        if (kClassDeclaration.getKind() != ElementKind.CLASS) {
            return;
        }

        final var constructors = KTreeFilter.constructors(kClassDeclaration);

        if (constructors.isEmpty()) {
            return;
        }

        MethodDeclaration resolvedConstructor = null;

        for (final var constructor : constructors) {
            if (resolvedConstructor == null) {
                resolvedConstructor = constructor;
            } else if (constructor.getParameters().size() > resolvedConstructor.getParameters().size()) {
                resolvedConstructor = constructor;
            }
        }

        if (resolvedConstructor.getParameters().isEmpty()) {
            return;
        }

        kClassDeclaration.removeEnclosed(resolvedConstructor);
        kClassDeclaration.setPrimaryConstructor(resolvedConstructor);

        if (addJvmOverloads(resolvedConstructor)) {
            resolvedConstructor.annotation("kotlin.jvm.JvmOverloads");
        }

        final var parameterNames = resolvedConstructor.getParameters().stream()
                .map(VariableDeclaration::getName)
                .toList();

        final var fieldsToRemove = KTreeFilter.fields(kClassDeclaration).stream()
                .filter(field -> parameterNames.contains(field.getName()))
                .toList();

        fieldsToRemove.forEach(kClassDeclaration::removeEnclosed);
    }

    private void removeGetters(final ClassDeclaration classDeclaration) {
        final var getterNames = new ArrayList<String>();

        final var primaryConstructor = classDeclaration.getPrimaryConstructor();

        if (primaryConstructor != null) {
            getterNames.addAll(primaryConstructor.getParameters().stream()
                    .map(parameter -> "get" + firstUpper(parameter.getName()))
                    .toList());
        }

        getterNames.addAll(KTreeFilter.fields(classDeclaration).stream()
                .map(field -> "get" + firstUpper(field.getName()))
                .toList()
        );

        final var gettersToRemove = KTreeFilter.methods(classDeclaration).stream()
                .filter(methodDeclaration -> getterNames.contains(methodDeclaration.getSimpleName().toString()))
                .toList();

        gettersToRemove.forEach(classDeclaration::removeEnclosed);
    }

    private void removeSetters(final ClassDeclaration classDeclaration) {
        KTreeFilter.methods(classDeclaration).stream()
                .filter(this::isSetter)
                .forEach(classDeclaration::removeEnclosed);
    }

    private boolean isSetter(final MethodDeclaration methodDeclaration) {
        if (methodDeclaration.getParameters().size() != 1) {
            return false;
        }

        final var methodName = methodDeclaration.getSimpleName().toString();

        return methodName.startsWith("set")
                && methodName.length() >= 4;
    }

    private boolean addJvmOverloads(final MethodDeclaration primaryConstructor) {
        return !primaryConstructor.getParameters().isEmpty()
                && primaryConstructor.getParameters().stream()
                .allMatch(it -> it.getInitExpression().isPresent());
    }

    private Set<Modifier> convertModifiers(final Set<Modifier> modifiers,
                                           final Predicate<Modifier> filter) {
        return modifiers.stream()
                .filter(filter)
                .filter(modifier -> modifier != Modifier.PUBLIC)
                .map(Modifier::toKotlinModifier)
                .collect(Collectors.toUnmodifiableSet());
    }

    //Statements

    @Override
    public Statement visitUnknown(final Statement statement, final CodeContext context) {
        return statement;
    }

    @Override
    public Statement visitExpressionStatement(final ExpressionStatement expressionStatement, final CodeContext context) {
        final var convertedExpression = (Expression) expressionStatement.getExpression()
                .accept(this, context);
        return new ExpressionStatement(convertedExpression);
    }

    @Override
    public Statement visitBlockStatement(final BlockStatement blockStatement, final CodeContext context) {
        final var newStatements = blockStatement.getStatements().stream()
                .map(statement -> (Statement) statement.accept(this, context))
                .toList();
        return new BlockStatement(newStatements);
    }

    @Override
    public Statement visitReturnStatement(final ReturnStatement returnStatement, final CodeContext context) {
        final var expression = (Expression) returnStatement.getExpression().accept(this, context);
        return new ReturnStatement(expression);
    }

    @Override
    public Statement visitIfStatement(final IfStatement ifStatement, final CodeContext context) {
        final var childContext = context.child();
        final var condition = (Expression) ifStatement.getCondition().accept(this, childContext);
        final var body = (BlockStatement) ifStatement.getBody().accept(this, childContext);
        return new IfStatement(condition, body);
    }

    //Expressions
    @Override
    public Expression visitUnknown(final Expression expression, final CodeContext context) {
        return expression;
    }

    @Override
    public Expression visitMethodCall(final MethodCallExpression methodCallExpression, final CodeContext context) {
        if (isGetterCall(methodCallExpression)) {
            final var propertyAccessExpression = new PropertyAccessExpression();
            propertyAccessExpression.setTarget(methodCallExpression.getTarget().orElse(null));
            final var propertyName = methodNameToPropertyName(methodCallExpression.getMethodName().getName());
            propertyAccessExpression.setName(new IdentifierExpression(propertyName));
            return propertyAccessExpression;
        }

        final var target = (Expression) methodCallExpression.getTarget()
                .map(it -> it.accept(this, context))
                .orElse(null);

        final var convertedArguments = methodCallExpression.getArguments().stream()
                .map(argument -> (Expression) argument.accept(this, context))
                .toList();

        final var convertedMethodCallOptional = convertMethodCallExpression(
                methodCallExpression.getMethodName().getName(),
                target,
                convertedArguments,
                context
        );

        return convertedMethodCallOptional.orElseGet(() ->
            new MethodCallExpression(target, methodCallExpression.getMethodName().getName(), convertedArguments)
        );
    }

    private String methodNameToPropertyName(final String methodName) {
        return StringUtils.firstLower(methodName.substring(3));
    }

    private boolean isGetterCall(final MethodCallExpression methodCallExpression) {
        if (!methodCallExpression.getArguments().isEmpty()) {
            return false;
        }

        final var symbol = methodCallExpression.getMethodName().getSymbol();

        if (symbol != null) {
            return symbol instanceof MethodSymbol methodSymbol
                    && methodSymbol.getParameters().isEmpty()
                    && methodSymbol.getReturnType().getKind() != TypeKind.VOID;
        } else {
            return methodCallExpression.getArguments().isEmpty()
                    && methodCallExpression.getMethodName().getName().startsWith("get");
        }
    }


    private Optional<Expression> convertMethodCallExpression(final String methodName,
                                                             final Expression target,
                                                             final List<Expression> arguments,
                                                             final CodeContext context) {
        if (target == null) {
            return Optional.empty();
        }

        if (target instanceof IdentifierExpression targetName) {
           final var name = targetName.getName();
           final var targetTypeOptional = context.resolveLocalVariable(name);

           if (targetTypeOptional.isEmpty()) {
               return Optional.empty();
           }

           final var targetType = targetTypeOptional.get();

           if (!(targetType instanceof final DeclaredType targetDeclaredType)) {
               return Optional.empty();
           }

           final var classname = Elements.getQualifiedName(targetDeclaredType.asElement());

           if (!"java.lang.StringBuffer".equals(classname.toString())) {
               return Optional.empty();
           }
           return convertStringBufferMethodCallExpression(
                   methodName,
                   target,
                   arguments
           );
        }

        return Optional.empty();
    }

    private Optional<Expression> convertStringBufferMethodCallExpression(final String methodName,
                                                                         final Expression target,
                                                                         final List<Expression> arguments) {
        if ("charAt".equals(methodName)) {
            final var convertedExpression = arguments.get(0);
            return Optional.of(new ArrayAccessExpression(target, convertedExpression));
        } else if ("length".equals(methodName)) {
            return Optional.of(new FieldAccessExpression(
                    target,
                    "length"
            ));
        }

        return Optional.empty();
    }

    @Override
    public Statement visitVariableDeclaration(final VariableDeclaration jVariableDeclaration, final CodeContext context) {
        final var kind = jVariableDeclaration.getKind();

        var initExpression = jVariableDeclaration.getInitExpression()
                .map(it -> (Expression) it.accept(this, context))
                .orElse(null);

        final var modifiers = jVariableDeclaration.getModifiers();
        final Set<Modifier> newModifiers = new HashSet<>(convertModifiers(modifiers, modifier ->
                modifier != Modifier.PRIVATE
        ));

        if (kind == ElementKind.PARAMETER) {
            newModifiers.remove(Modifier.VAL);
        } else if (!newModifiers.contains(Modifier.VAL)) {
            newModifiers.add(Modifier.VAR);
        }

        final var localVariableName = jVariableDeclaration.getName();
        final var newLocalVariableType = (Expression) jVariableDeclaration.getVarType().accept(this, context);

        context.defineLocalVariable(newLocalVariableType.getType(), localVariableName);

        if (initExpression == null && jVariableDeclaration.getKind() == ElementKind.FIELD) {
            initExpression = defaultValueResolver.createDefaultValue(newLocalVariableType.getType());
        }

        return new VariableDeclaration()
                .kind(jVariableDeclaration.getKind())
                .modifiers(newModifiers)
                .varType(newLocalVariableType)
                .name(localVariableName)
                .initExpression(initExpression)
                .symbol(jVariableDeclaration.getSymbol());
    }

    @Override
    public Expression visitNamedMethodArgumentExpression(final NamedMethodArgumentExpression namedMethodArgumentExpression, final CodeContext context) {
        return namedMethodArgumentExpression;
    }

    @Override
    public Expression visitIdentifierExpression(final IdentifierExpression identifierExpression, final CodeContext context) {
        return identifierExpression;
    }

    @Override
    public Expression visitFieldAccessExpression(final FieldAccessExpression fieldAccessExpression, final CodeContext context) {
        return fieldAccessExpression;
    }

    @Override
    public Expression visitBinaryExpression(final BinaryExpression binaryExpression, final CodeContext context) {
        final var left = (Expression) binaryExpression.getLeft().accept(this, context);
        final var right = (Expression) binaryExpression.getRight().accept(this, context);
        return new BinaryExpression(left, right, binaryExpression.getOperator());
    }

    @Override
    public Object visitUnaryExpression(final UnaryExpression unaryExpression, final CodeContext context) {
        final var expression = (Expression) unaryExpression.getExpression().accept(this, context);
        return new UnaryExpression()
                .operator(unaryExpression.getOperator())
                .expression(expression);
    }

    @Override
    public Expression visitLiteralExpression(final LiteralExpression literalExpression, final CodeContext context) {
        if (literalExpression.getLiteralType() == LiteralType.CLASS) {
            final var classLiteral = (ClassLiteralExpression) literalExpression;
            final var type = classLiteral.getType();
            final var convertedType = type.accept(this, context);

            if (convertedType instanceof PrimitiveType) {
                throw new UnsupportedOperationException();
            }

            //return LiteralExpression.createClassLiteralExpression(convertedType);
            throw new UnsupportedOperationException();
        }
        return literalExpression;
    }

    @Override
    public AnnotationMirror visitAnnotation(final AnnotationMirror annotationExpression, final CodeContext context) {
        final var convertedElementValues = annotationExpression.getElementValues().entrySet().stream()
                .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            it -> (AnnotationValue) it.getValue().accept(this, context)
                        )
                );

        return Attribute.compound(
                (ClassSymbol) annotationExpression.getAnnotationType().asElement(),
                convertedElementValues
        );
    }

    @Override
    public Expression visitArrayAccessExpression(final ArrayAccessExpression arrayAccessExpression, final CodeContext context) {
        final var arrayExpression = (Expression) arrayAccessExpression.getArrayExpression().accept(this, context);
        final var indexExpression = (Expression) arrayAccessExpression.getIndexExpression().accept(this, context);
        return new ArrayAccessExpression(arrayExpression, indexExpression);
    }

    @Override
    public Expression visitArrayInitializerExpression(final ArrayInitializerExpression arrayInitializerExpression, final CodeContext context) {
        final var newValues = arrayInitializerExpression.getValues().stream()
                .map(it -> (Expression) it.accept(this, context))
                .toList();
        return new ArrayInitializerExpression(newValues);
    }

    @Override
    public Expression visitNewClassExpression(final NewClassExpression newClassExpression, final CodeContext context) {
        final var newClassType = (ClassOrInterfaceTypeExpression) newClassExpression.getClazz().accept(this, context);
        final var newArgs = newClassExpression.getArguments().stream()
                .map(arg -> (Expression) arg.accept(this, context))
                .toList();
        return new NewClassExpression(newClassType, newArgs);
    }

    @Override
    public Expression visitAnnotatedType(final AnnotatedTypeExpression annotatedTypeExpression, final CodeContext context) {
        final var identifier = (Expression) annotatedTypeExpression.getIdentifier().accept(this, context);
        final var annotations = annotatedTypeExpression.getAnnotations().stream()
                .map(annotation -> (AnnotationExpression) annotation.accept(this, context))
                .toList();

        final var newAnnotatedTypeExpression = new AnnotatedTypeExpression(identifier, annotations);
        newAnnotatedTypeExpression.type(annotatedTypeExpression.getType());

        return newAnnotatedTypeExpression;
    }

    //Types
    @Override
    public TypeMirror visitUnknown(final TypeMirror type,
                                   final CodeContext context) {
        return type;
    }

    @Override
    public Object visitClassOrInterfaceTypeExpression(final ClassOrInterfaceTypeExpression classOrInterfaceTypeExpression,
                                                      final CodeContext context) {
        final var qualifiedName = classOrInterfaceTypeExpression.getName();
        final var kotlinTypeName = JAVA_TO_KOTLIN_TYPE.get(qualifiedName.toString());
        final var isNullable = classOrInterfaceTypeExpression.getType().isNullable();

        ClassOrInterfaceTypeExpression convertedTypeExpression;

        final List<TypeExpression> typeArgs;
        final TypeMirror[] typeArgTypes;

        if (classOrInterfaceTypeExpression.getTypeArguments() != null) {
            typeArgs = classOrInterfaceTypeExpression.getTypeArguments().stream()
                    .map(typeArg -> (TypeExpression) typeArg.accept(this, context))
                    .toList();
            typeArgTypes = typeArgs.stream()
                    .map(Tree::getType)
                    .toArray(TypeMirror[]::new);
        } else {
            typeArgs = null;
            typeArgTypes = null;
        }

        if (kotlinTypeName != null) {
            convertedTypeExpression = new ClassOrInterfaceTypeExpression(kotlinTypeName);
            var declaredType = typeArgTypes != null
                    ? types.getDeclaredType(elements.getTypeElement(kotlinTypeName), typeArgTypes)
                    : types.getDeclaredType(elements.getTypeElement(kotlinTypeName));
            declaredType = isNullable ? declaredType.asNullableType() : declaredType.asNonNullableType();
            convertedTypeExpression.type(declaredType);
        } else {
            final var oldType = (ClassType) classOrInterfaceTypeExpression.getType();

            convertedTypeExpression = new ClassOrInterfaceTypeExpression(classOrInterfaceTypeExpression.getName());

            var newType = types.getDeclaredType(
                    oldType.asElement(),
                    typeArgTypes
            );
            newType = isNullable ? newType.asNullableType() : newType.asNonNullableType();
            convertedTypeExpression.type(newType);
        }

        if (typeArgs != null) {
            typeArgs.forEach(convertedTypeExpression::typeArgument);
        }

        convertedTypeExpression.setNullable(isNullable);

        return convertedTypeExpression;
    }

    @Override
    public TypeMirror visitDeclared(final DeclaredType declaredType,
                                    final CodeContext context) {
        final var qualifiedName = Elements.getQualifiedName(declaredType.asElement());
        final var kotlinTypeName = JAVA_TO_KOTLIN_TYPE.get(qualifiedName.toString());
        TypeMirror convertedType;

        final var typeArgs = declaredType.getTypeArguments().stream()
                .map(typeArg -> typeArg.accept(this, context))
                .toArray(TypeMirror[]::new);

        if (kotlinTypeName != null) {
            convertedType = types.getDeclaredType(elements.getTypeElement(kotlinTypeName), typeArgs);
        } else {
            convertedType = types.getDeclaredType((TypeElement) declaredType.asElement(), typeArgs);
        }

        if (declaredType.isNullable()) {
            convertedType = convertedType.asNullableType();
        } else {
            convertedType = convertedType.asNonNullableType();
        }

        return convertedType;
    }

    @Override
    public TypeMirror visitExecutable(final ExecutableType executableType,
                                      final CodeContext context) {
        return executableType;
    }

    @Override
    public TypeMirror visitArray(final ArrayType t, final CodeContext codeContext) {
        final var componentType = t.getComponentType();
        final var convertedComponentType = componentType.accept(this, codeContext);

        if (componentType instanceof PrimitiveType) {
            switch (componentType.getKind()) {
                case BYTE,
                        CHAR,
                        SHORT,
                        INT,
                        LONG,
                        FLOAT,
                        DOUBLE,
                        BOOLEAN -> {
                    return types.getArrayType(convertedComponentType);
                }
            }
        }

        return types.getArrayType(convertedComponentType);
    }


    @Override
    public TypeMirror visitPrimitive(final PrimitiveType t,
                                     final CodeContext codeContext) {
        return switch (t.getKind()) {
            case BOOLEAN -> getDeclaredType("kotlin.Boolean");
            case BYTE -> getDeclaredType("kotlin.Byte");
            case SHORT -> getDeclaredType("kotlin.Short");
            case INT -> getDeclaredType("kotlin.Int");
            case LONG -> getDeclaredType("kotlin.Long");
            case CHAR -> getDeclaredType("kotlin.Char");
            case FLOAT -> getDeclaredType("kotlin.Float");
            case DOUBLE -> getDeclaredType("kotlin.Double");
            default -> throw new IllegalArgumentException(String.format("Invalid primitive type %s", t.getKind()));
        };
    }

    private DeclaredType getDeclaredType(final String name) {
        return types.getDeclaredType(elements.getTypeElement(name));
    }

    @Override
    public TypeMirror visitWildcard(final WildcardType wildcardType,
                                    final CodeContext context) {
        return wildcardType;
    }

    @Override
    public Object visit(final AnnotationValue av, final CodeContext codeContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visit(final AnnotationValue av) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitBoolean(final boolean value, final CodeContext param) {
        return Attribute.constant(value);
    }

    @Override
    public Object visitByte(final byte b, final CodeContext codeContext) {
        return Attribute.constant(b);
    }

    @Override
    public Object visitChar(final char value, final CodeContext param) {
        return Attribute.constant(value);
    }

    @Override
    public Object visitShort(final short value, final CodeContext param) {
        return Attribute.constant(value);
    }

    @Override
    public Object visitInt(final int value, final CodeContext param) {
        return Attribute.constant(value);
    }

    @Override
    public Object visitLong(final long value, final CodeContext param) {
        return Attribute.constant(value);
    }

    @Override
    public Object visitFloat(final float value, final CodeContext param) {
        return Attribute.constant(value);
    }

    @Override
    public Object visitDouble(final double value, final CodeContext param) {
        return Attribute.constant(value);
    }

    @Override
    public Object visitString(final String value, final CodeContext param) {
        return Attribute.constant(value);
    }

    @Override
    public Object visitType(final TypeMirror t, final CodeContext codeContext) {
        return Attribute.clazz(t);
    }

    @Override
    public Object visitUnknown(final AnnotationValue av, final CodeContext codeContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitEnumConstant(final VariableElement enumValue, final CodeContext param) {
        return Attribute.createEnumAttribute(enumValue);
    }

    @Override
    public Object visitArray(final List<? extends AnnotationValue> array, final CodeContext param) {
        return Attribute.array(array);
    }

    @Override
    public TypeMirror visit(final TypeMirror t, final CodeContext codeContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TypeMirror visitNull(final NullType t, final CodeContext codeContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TypeMirror visitNoType(final NoType t,
                                  final CodeContext codeContext) {
        return t.getKind() == TypeKind.VOID
                ? UnitType.INSTANCE
                : t;
    }

    @Override
    public TypeMirror visitError(final ErrorType t,
                                 final CodeContext codeContext) {
        return t;
    }

    @Override
    public TypeMirror visitTypeVariable(final TypeVariable t,
                                        final CodeContext codeContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TypeMirror visitUnion(final UnionType t,
                                 final CodeContext codeContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TypeMirror visitIntersection(final IntersectionType t,
                                        final CodeContext codeContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TypeMirror visitVarType(final VarType varType, final CodeContext codeContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Statement visitClassDeclaration(final ClassDeclaration classDeclaration,
                                           final CodeContext context) {
        ClassDeclaration kClassDeclaration;

        if (isUtilityClass(classDeclaration)) {
            final var modifiers = classDeclaration.getModifiers().stream()
                    .filter(modifier -> modifier != Modifier.FINAL)
                    .map(Modifier::toKotlinModifier)
                    .collect(Collectors.toSet());

            kClassDeclaration = new ClassDeclaration()
                    .simpleName(classDeclaration.getSimpleName())
                            .kind(ElementKind.OBJECT)
                                    .modifiers(modifiers);

            final var childContext = context.child(classDeclaration);

            final var newEnclosedElements = classDeclaration.getEnclosed().stream()
                    .filter(enclosed -> !(enclosed instanceof MethodDeclaration methodDeclaration)
                            || methodDeclaration.getKind() != ElementKind.CONSTRUCTOR)
                    .map(enclosed -> (Tree) enclosed.accept(this, childContext))
                    .toList();

            newEnclosedElements.forEach(newEnclosedElement -> {
                if (newEnclosedElement instanceof MethodDeclaration methodDeclaration) {
                    methodDeclaration.removeModifier(Modifier.STATIC);
                }
            });

            kClassDeclaration.addEnclosed(newEnclosedElements);
        } else {
            final var modifiers = classDeclaration.getModifiers().stream()
                    .map(Modifier::toKotlinModifier)
                    .collect(Collectors.toSet());

            kClassDeclaration = new ClassDeclaration()
                    .simpleName(classDeclaration.getSimpleName())
                            .kind(classDeclaration.getKind())
                                    .modifiers(modifiers);

            final Expression extending =
                    classDeclaration.getExtending() != null
                        ? (Expression) classDeclaration.getExtending().accept(this, context)
                        : null;
            final var implementing = classDeclaration.getImplementing().stream()
                    .map(expression -> (Expression) expression.accept(this, context))
                    .toList();

            kClassDeclaration.setExtending(extending);
            implementing.forEach(kClassDeclaration::addImplement);

            final var childContext = context.child(classDeclaration);

            final var newEnclosedElements = classDeclaration.getEnclosed().stream()
                    .map(it -> (Tree) it.accept(this, childContext))
                    .toList();

            kClassDeclaration.addEnclosed(newEnclosedElements);

            conditionalAddPrimaryConstructor(kClassDeclaration);

            final var primaryConstructor = (MethodDeclaration) kClassDeclaration.getPrimaryConstructor();

            if (primaryConstructor != null) {
                final var parameters = primaryConstructor.getParameters().stream()
                                .collect(Collectors.toMap(
                                        VariableDeclaration::getName,
                                        parameter -> parameter
                                ));

                JTreeFilter.fields(classDeclaration).forEach(field -> {
                    final var name = field.getName();
                    final var parameter = parameters.get(name);

                    if (parameter != null) {
                        if (field.getModifiers().contains(Modifier.FINAL)) {
                            parameter.modifier(Modifier.VAL);
                        } else {
                            parameter.modifier(Modifier.VAR);
                        }
                    }
                });
            }

            removeGetters(kClassDeclaration);
            removeSetters(kClassDeclaration);
        }

        final var newEnclosing = (Tree) classDeclaration.getEnclosing().accept(this, context);
        kClassDeclaration.setEnclosing(newEnclosing);

        final var classSymbol = classDeclaration.getClassSymbol();
        classSymbol.removeModifier(Modifier.PUBLIC);
        kClassDeclaration.removeModifier(Modifier.PUBLIC);
        kClassDeclaration.classSymbol(toKClassSymbol(classSymbol));

        classDeclaration.getAnnotations().stream()
                .map(annotation -> (AnnotationExpression) annotation.accept(this, context))
                .forEach(kClassDeclaration::annotation);

        return kClassDeclaration;
    }

    private ClassSymbol toKClassSymbol(final ClassSymbol jClassSymbol) {
        final var kClassSymbol = new KClassSymbolBuilder()
                .kind(jClassSymbol.getKind())
                .simpleName(jClassSymbol.getSimpleName())
                .nestingKind(jClassSymbol.getNestingKind())
                .enclosingElement(convert(jClassSymbol.getEnclosingElement()))
                .build();

        kClassSymbol.setType(jClassSymbol.asType());

        return kClassSymbol;
    }

    private AbstractSymbol convert(final Element abstractSymbol) {
        if (abstractSymbol instanceof ClassSymbol classSymbol) {
            return toKClassSymbol(classSymbol);
        } else {
            return (AbstractSymbol) abstractSymbol;
        }
    }

    @Override
    public Object visitPrimitiveTypeExpression(final PrimitiveTypeExpression primitiveTypeExpression, final CodeContext param) {
        return primitiveTypeExpression;
    }

    @Override
    public Object visitNoType(final NoTypeExpression noTypeExpression, final CodeContext param) {
        return noTypeExpression;
    }

    @Override
    public Object visitAnnotationExpression(final AnnotationExpression annotationExpression, final CodeContext context) {
        final var annotationType = (ClassOrInterfaceTypeExpression) annotationExpression.getAnnotationType().accept(this, context);
        final var arguments = annotationExpression.getArguments();
        return new AnnotationExpression(annotationType,arguments);
    }

    @Override
    public Object visitWildCardTypeExpression(final WildCardTypeExpression wildCardTypeExpression, final CodeContext param) {
        return wildCardTypeExpression;
    }

}
