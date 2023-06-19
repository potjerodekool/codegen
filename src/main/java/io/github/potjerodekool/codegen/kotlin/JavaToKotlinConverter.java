package io.github.potjerodekool.codegen.kotlin;

import io.github.potjerodekool.codegen.*;
import io.github.potjerodekool.codegen.model.Attribute;
import io.github.potjerodekool.codegen.model.CompilationUnit;
import io.github.potjerodekool.codegen.model.element.*;
import io.github.potjerodekool.codegen.model.symbol.MethodSymbol;
import io.github.potjerodekool.codegen.model.symbol.ClassSymbol;
import io.github.potjerodekool.codegen.model.symbol.VariableSymbol;
import io.github.potjerodekool.codegen.model.tree.TreeVisitor;
import io.github.potjerodekool.codegen.model.tree.expression.*;
import io.github.potjerodekool.codegen.model.tree.type.AnnotatedTypeExpression;
import io.github.potjerodekool.codegen.model.tree.type.PrimitiveTypeExpression;
import io.github.potjerodekool.codegen.model.tree.statement.*;
import io.github.potjerodekool.codegen.model.type.*;
import io.github.potjerodekool.codegen.model.type.kotlin.UnitType;
import io.github.potjerodekool.codegen.model.util.Elements;
import io.github.potjerodekool.codegen.model.util.StringUtils;
import io.github.potjerodekool.codegen.model.util.type.Types;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class JavaToKotlinConverter implements ElementVisitor<Void, CodeContext>,
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
        newCompilationUnit.setPackageElement(compilationUnit.getPackageElement());

        final var codeContext = new CodeContext(newCompilationUnit);

        compilationUnit.getElements()
                        .forEach(element -> element.accept(this, codeContext));
        return newCompilationUnit;
    }

    //Elements

    @Override
    public Void visitPackage(final PackageElement packageElement, final CodeContext context) {
        return null;
    }

    @Override
    public Void visitType(final TypeElement typeElement, final CodeContext context) {
        if (isUtilityClass(typeElement)) {
            typeElement.getEnclosedElements().stream()
                    .filter(element -> element.getKind() != ElementKind.CONSTRUCTOR)
                    .forEach(element -> element.accept(this, context));
        } else {
            final var parentAstNode = context.getAstNode();
            final var classSymbol = (ClassSymbol) typeElement;

            final var childContext = context.child(classSymbol);

            classSymbol.removeModifier(Modifier.PUBLIC);
            final var primaryConstructor = convertFirstConstructorToPrimaryConstructor(classSymbol, childContext);

            final var enclosedElements = typeElement.getEnclosedElements().stream().toList();
            enclosedElements.forEach(enclosedElement -> enclosedElement.accept(this, childContext));
            removeFieldsWithGetterAndSetters(classSymbol, primaryConstructor);

            if (parentAstNode instanceof CompilationUnit cu) {
                cu.addElement(typeElement);
            }
        }
        return null;
    }

    @Override
    public Void visitExecutable(final ExecutableElement methodElement,
                                final CodeContext context) {
        final var parentAstNode = context.getAstNode();

        final var newMethod = methodElement.getKind() == ElementKind.METHOD
                ? MethodSymbol.createMethod(methodElement.getSimpleName(), methodElement.getReturnType().accept(this, context))
                : MethodSymbol.createConstructor(methodElement.getSimpleName());

        final var childContext = context.child(newMethod);

        methodElement.getParameters()
                .forEach(parameter -> parameter.accept(this, childContext));

        ((MethodSymbol)methodElement).getBody().ifPresent(body -> {
            final var newBody = (BlockStatement) body.accept(this, childContext);
            newMethod.setBody(newBody);
        });

        newMethod.addModifiers(convertModifiers(methodElement.getModifiers(),
                it -> !(it == Modifier.DEFAULT || it == Modifier.STATIC)));

        if (parentAstNode instanceof ClassSymbol te) {
            te.removeEnclosedElement(methodElement);
            te.addEnclosedElement(newMethod);
        } else if (parentAstNode instanceof CompilationUnit cu) {
            cu.removeElement(methodElement);
            cu.addElement(newMethod);
        }

        return null;
    }

    @Override
    public Void visitVariable(final VariableElement variableElement,
                              final CodeContext context) {
        final VariableSymbol newVariableElement;

        if (variableElement.getKind() == ElementKind.PARAMETER) {
            newVariableElement = VariableSymbol.createParameter(
                    variableElement.getSimpleName().toString(),
                    variableElement.asType().accept(this, context)
            );
        } else if (variableElement.getKind() == ElementKind.FIELD) {
            newVariableElement = VariableSymbol.createField(
                    variableElement.getSimpleName().toString(),
                    variableElement.asType().accept(this, context)
            );
        } else {
            throw new UnsupportedOperationException();
        }

        if (newVariableElement.getKind() == ElementKind.PARAMETER) {
            final var method = (MethodSymbol) context.getAstNode();
            method.addParameter(newVariableElement);
        } else if (newVariableElement.getKind() == ElementKind.FIELD) {
            final var clazz = (ClassSymbol) context.getAstNode();
            clazz.removeEnclosedElement(variableElement);
            clazz.addEnclosedElement(newVariableElement);
        }

        return null;
    }

    private boolean isUtilityClass(final TypeElement typeElement) {
        return typeElement.getEnclosedElements().stream()
                .filter(element -> element.getKind() != ElementKind.CONSTRUCTOR)
                .allMatch(element ->
                        element.getKind() == ElementKind.METHOD
                        && ((MethodSymbol)element).hasModifier(Modifier.STATIC)
                );
    }

    private @Nullable MethodSymbol convertFirstConstructorToPrimaryConstructor(final ClassSymbol typeElement,
                                                                               final CodeContext context) {
        final var firstConstructor = removeConstructors(typeElement);

        final var fields = ElementFilter.fields(typeElement).toList();

        if (typeElement.getKind() != ElementKind.INTERFACE) {
            final var primaryConstructor = typeElement.addPrimaryConstructor();

            fields.forEach(field -> primaryConstructor.addParameter(createParameter(field, context)));

            primaryConstructor.addAnnotation((ClassSymbol) elements.getTypeElement("kotlin.jvm.JvmOverloads"));
        }

        return firstConstructor;
    }

    private VariableSymbol createParameter(final VariableSymbol field,
                                           final CodeContext context) {
        final var paramType = (TypeMirror) field.asType().accept(this, context);

        final var fieldModifiers = field.getModifiers();

        final VariableSymbol parameter = VariableSymbol.createParameter(
                field.getSimpleName().toString(),
                paramType
        );

        /*TODO
        if (paramType.isNullable()) {
            parameter.setInitExpression(LiteralExpression.createNullLiteralExpression());
        } else {
            parameter.setInitExpression(defaultValueResolver.createDefaultValue(paramType));
        }
        */

        convertModifiers(
                fieldModifiers,
                modifier -> modifier != Modifier.PRIVATE
        ).forEach(parameter::addModifier);

        return parameter;
    }

    private Set<Modifier> convertModifiers(final Set<Modifier> modifiers,
                                           final Predicate<Modifier> filter) {
        return modifiers.stream()
                .filter(filter)
                .filter(modifier -> modifier != Modifier.PUBLIC)
                .collect(Collectors.toUnmodifiableSet());
    }

    private AnnotationMirror toFieldAnnotation(final AnnotationMirror annotation,
                                               final CodeContext context) {
        final var elementValues = annotation.getElementValues().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        it -> (AnnotationValue) it.getValue().accept(this, context)
                ));

        return Attribute.compound(
                (ClassSymbol) annotation.getAnnotationType().asElement(),
                elementValues,
                AnnotationTarget.FIELD
        );
    }

    private @Nullable MethodSymbol removeConstructors(final ClassSymbol typeElement) {
        final var constructors = ElementFilter.constructors(typeElement)
                .toList();

        if (constructors.isEmpty()) {
            return null;
        }

        final var firstConstructor = constructors.get(0);

        constructors.forEach(constructor -> {
            typeElement.removeEnclosedElement(constructor);
            constructor.setBody(null);
        });

        return firstConstructor;
    }

    private void removeFieldsWithGetterAndSetters(final ClassSymbol typeElement,
                                                  final @Nullable MethodSymbol primaryConstructor) {
        if (primaryConstructor == null) {
            return;
        }

        final var fields = ElementFilter.fields(typeElement).toList();
        removeGetterAndSetters(typeElement, fields);
        fields.forEach(typeElement::removeEnclosedElement);
    }

    private void removeGetterAndSetters(final ClassSymbol typeElement,
                                        final List<VariableSymbol> fields) {

        final var fieldMap = fields.stream()
                .collect(Collectors.toMap(
                        it -> it.getSimpleName().toString(),
                        Function.identity()
                ));

        final var getterNames = new ArrayList<String>();
        final var setterNames = new ArrayList<String>();

        fields.forEach(field -> {
            getterNames.add("get" + StringUtils.firstUpper(field.getSimpleName().toString()));
            getterNames.add(field.getSimpleName().toString());
        });

        fields.forEach(field -> {
            setterNames.add("set" + StringUtils.firstUpper(field.getSimpleName().toString()));
            setterNames.add(field.getSimpleName().toString());
        });

        var methods = ElementFilter.methods(typeElement).toList();

        methods.stream().filter(this::isGetter)
                .filter(getter -> getterNames.contains(getter.getSimpleName().toString()))
                .filter(getter -> {
                    final String getterName = getter.getSimpleName().toString();
                    final String fieldName = StringUtils.firstLower(getterName.startsWith("get") ? getterName.substring(3) : getterName);
                    final var field = fieldMap.get(fieldName);
                    return field != null && types.isSameType(getter.getReturnType(), field.asType());
                }).forEach(typeElement::removeEnclosedElement);

        methods.stream().filter(this::isSetter)
                .filter(setter -> setterNames.contains(setter.getSimpleName().toString()))
                .filter(setter -> {
                    final String setterName = setter.getSimpleName().toString();
                    final var isNormalSetter = setterName.startsWith("set");
                    final String fieldName = StringUtils.firstLower(isNormalSetter ? setterName.substring(3) : setterName);
                    final var field = fieldMap.get(fieldName);
                    return field != null && types.isSameType(setter.getParameters().get(0).asType(), field.asType()) && isNormalSetter;
                }).forEach(typeElement::removeEnclosedElement);
    }

    private boolean isGetter(final MethodSymbol method) {
        final var returnType = method.getReturnType();

        if (returnType.getKind() == TypeKind.VOID) {
            return false;
        }

        return method.getParameters().isEmpty();
    }

    private boolean isSetter(final MethodSymbol method) {
        return method.getParameters().size() == 1;
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
        final var target = (Expression) methodCallExpression.getTarget()
                .map(it -> it.accept(this, context))
                .orElse(null);

        final var convertedArguments = methodCallExpression.getArguments().stream()
                .map(argument -> (Expression) argument.accept(this, context))
                .toList();

        final var convertedMethodCallOptional = convertMethodCallExpression(
                methodCallExpression.getMethodName(),
                target,
                convertedArguments,
                context
        );

        return convertedMethodCallOptional.orElseGet(() ->
            new MethodCallExpression(target, methodCallExpression.getMethodName(), convertedArguments)
        );
    }

    private Optional<Expression> convertMethodCallExpression(final String methodName,
                                                             final @Nullable Expression target,
                                                             final List<Expression> arguments,
                                                             final CodeContext context) {
        if (target == null) {
            return Optional.empty();
        }

        if (target instanceof NameExpression targetName) {
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
    public Statement visitVariableDeclaration(final VariableDeclaration variableDeclaration, final CodeContext context) {
        final var initExpression = variableDeclaration.getInitExpression()
                .map(it -> (Expression) it.accept(this, context))
                .orElse(null);

        final var modifiers = variableDeclaration.getModifiers();
        final var newModifiers = convertModifiers(modifiers, it -> it != Modifier.FINAL);

        final var localVariableName = variableDeclaration.getName();
        final var newLocalVariableType = (Expression) variableDeclaration.getType().accept(this, context);

        context.defineLocalVariable((TypeMirror) newLocalVariableType, localVariableName);

        return new VariableDeclaration(
                variableDeclaration.getKind(),
                newModifiers,
                newLocalVariableType,
                localVariableName,
                initExpression,
                variableDeclaration.getSymbol()
        );
    }

    @Override
    public Expression visitNamedMethodArgumentExpression(final NamedMethodArgumentExpression namedMethodArgumentExpression, final CodeContext context) {
        return namedMethodArgumentExpression;
    }

    @Override
    public Expression visitNameExpression(final NameExpression nameExpression, final CodeContext context) {
        return nameExpression;
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
        final var newClassType = (DeclaredType) newClassExpression.getClassType().accept(this, context);
        return new NewClassExpression(newClassType);
    }

    @Override
    public Expression visitAnnotatedType(final AnnotatedTypeExpression annotatedTypeExpression, final CodeContext param) {
        throw new UnsupportedOperationException();
    }

    //Types
    @Override
    public TypeMirror visitUnknown(final TypeMirror type,
                                   final CodeContext context) {
        return type;
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
    public TypeMirror visitVarType(final VarTypeImpl varType, final CodeContext codeContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Statement visitClassDeclaration(final ClassDeclaration classDeclaration, final CodeContext param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitPrimitiveTypeExpression(final PrimitiveTypeExpression primitiveTypeExpression, final CodeContext param) {
        throw new UnsupportedOperationException();
    }
}
