package io.github.potjerodekool.codegen.resolve;

import io.github.potjerodekool.codegen.DefaultDiagnostic;
import io.github.potjerodekool.codegen.Diagnostic;
import io.github.potjerodekool.codegen.DiagnosticListener;
import io.github.potjerodekool.codegen.model.CompilationUnit;
import io.github.potjerodekool.codegen.model.element.ElementKind;
import io.github.potjerodekool.codegen.model.element.Name;
import io.github.potjerodekool.codegen.model.element.NestingKind;
import io.github.potjerodekool.codegen.model.symbol.AbstractSymbol;
import io.github.potjerodekool.codegen.model.symbol.ClassSymbol;
import io.github.potjerodekool.codegen.model.symbol.PackageSymbol;
import io.github.potjerodekool.codegen.model.symbol.VariableSymbol;
import io.github.potjerodekool.codegen.model.tree.*;
import io.github.potjerodekool.codegen.model.tree.expression.*;
import io.github.potjerodekool.codegen.model.tree.statement.*;
import io.github.potjerodekool.codegen.model.tree.type.*;
import io.github.potjerodekool.codegen.model.type.ClassType;
import io.github.potjerodekool.codegen.model.type.ErrorTypeImpl;
import io.github.potjerodekool.codegen.model.type.TypeKind;
import io.github.potjerodekool.codegen.model.type.TypeMirror;
import io.github.potjerodekool.codegen.model.util.Elements;
import io.github.potjerodekool.codegen.model.util.QualifiedName;
import io.github.potjerodekool.codegen.model.util.SymbolTable;
import io.github.potjerodekool.codegen.model.util.type.Types;

public class Resolver implements TreeVisitor<Object, Scope> {

    protected final Elements elements;
    protected final Types types;
    protected final SymbolTable symbolTable;
    private final DiagnosticListener<Source> diagnosticListener;
    private final ClassFinder classFinder;

    public Resolver(final Elements elements,
                    final Types types,
                    final SymbolTable symbolTable) {
        this(elements, types, symbolTable, NoOpDiagnosticListener.getInstance());
    }

    public Resolver(final Elements elements,
                    final Types types,
                    final SymbolTable symbolTable,
                    final DiagnosticListener<Source> diagnosticListener) {
        this.elements = elements;
        this.types = types;
        this.symbolTable = symbolTable;
        this.diagnosticListener = diagnosticListener;
        this.classFinder = new ClassFinder(elements);
    }

    public void resolve(final CompilationUnit compilationUnit) {
        compilationUnit.getDefinitions().forEach(this::resolve);
    }

    public void resolve(final Tree tree) {
        tree.accept(this, null);
    }

    @Override
    public Object visitPackageDeclaration(final PackageDeclaration packageDeclaration, final Scope scope) {
        //TODO remove code. Should use Enter.
        if (packageDeclaration.getPackageSymbol() == null) {
            var packageSymbol = symbolTable.enterPackage(null, Name.of(packageDeclaration.getName().getName()));
            packageDeclaration.setPackageSymbol(packageSymbol);
        }
        return null;
    }

    @Override
    public Object visitClassDeclaration(final ClassDeclaration classDeclaration, final Scope scope) {
        //TODO remove if. Enter should be used.
        if (classDeclaration.getClassSymbol() == null) {
            var enclosing = classDeclaration.getEnclosing();
            final AbstractSymbol enclosingElement;
            final NestingKind nestingKind;

            if (enclosing == null) {
                enclosingElement = symbolTable.enterClass(null, Name.of(""));
                nestingKind = NestingKind.TOP_LEVEL;
            } else if (enclosing instanceof PackageDeclaration packageDeclaration) {
                enclosingElement = symbolTable.enterPackage(null, Name.of(packageDeclaration.getName().getName()));
                nestingKind = NestingKind.TOP_LEVEL;
            } else {
                enclosingElement = ((ClassDeclaration) enclosing).getClassSymbol();
                nestingKind = NestingKind.MEMBER;
            }

            final var qualifiedName = new QualifiedName(
                    Name.of(enclosingElement.getQualifiedName()),
                    classDeclaration.getSimpleName()
            );

            final var classSymbol = symbolTable.enterClass(null, Name.of(qualifiedName.asString()));
            final var classType = new ClassType(classSymbol, true);
            classSymbol.setType(classType);

            classSymbol.setNestingKind(nestingKind);
            classDeclaration.classSymbol(classSymbol);
        }

        final var classSymbol = classDeclaration.getClassSymbol();
        final var classScope = classSymbol.members();

        if (classDeclaration.getExtending() != null) {
            classDeclaration.getExtending().accept(this, classScope);
        }

        classDeclaration.getImplementing().forEach(implemention -> implemention.accept(this, classScope));

        classDeclaration.getAnnotations().forEach(annotationExpression -> annotationExpression.accept(this, classScope));
        classDeclaration.getEnclosed().forEach(enclosed -> enclosed.accept(this, classScope));
        return null;
    }

    private Scope findScope(final Tree tree) {
        if (tree instanceof MethodDeclaration methodDeclaration) {
            return methodDeclaration.getMethodSymbol().scope;
        } else if (tree instanceof ClassDeclaration classDeclaration) {
            return classDeclaration.getClassSymbol().scope;
        } else {
            throw new UnsupportedOperationException("Unsupported tree: " + tree);
        }
    }


    @Override
    public Object visitMethodDeclaration(final MethodDeclaration methodDeclaration, final Scope scope) {
        final var methodScope = findScope(methodDeclaration);

        methodDeclaration.getReturnType().accept(this, methodScope);
        final var returnType = methodDeclaration.getReturnType().getType();

        methodDeclaration.getTypeParameters().forEach(typeParam -> typeParam.accept(this, methodScope));
        methodDeclaration.getAnnotations().forEach(annotation -> annotation.accept(this, methodScope));
        methodDeclaration.getParameters().forEach(parameter -> parameter.accept(this, methodScope));

        methodDeclaration.getBody().ifPresent(body -> body.accept(this, methodScope));

        final var parameters = methodDeclaration.getParameters().stream()
                .map(parameter ->  (VariableSymbol) parameter.getSymbol())
                .toList();

        final var methodSymbol = methodDeclaration.getMethodSymbol();
        methodSymbol.setReturnType(returnType);

        methodSymbol.addModifiers(methodDeclaration.getModifiers());
        methodSymbol.addParameters(parameters);

        methodDeclaration.methodSymbol(methodSymbol);

        return null;
    }

    @Override
    public Object visitVariableDeclaration(final VariableDeclaration variableDeclaration, final Scope scope) {
        variableDeclaration.getVarType().accept(this, scope);
        variableDeclaration.getInitExpression().ifPresent(it -> it.accept(this, scope));

        variableDeclaration.getAnnotations().forEach(annotationExpression -> annotationExpression.accept(this, scope));

        var variableSymbol = variableDeclaration.getSymbol();

        if (variableSymbol == null) {
            variableSymbol = new VariableSymbol(
                    variableDeclaration.getKind(),
                    variableDeclaration.getName()
            );
            variableSymbol.addModifiers(variableDeclaration.getModifiers());
            variableDeclaration.symbol(variableSymbol);
        }

        variableSymbol.setType(variableDeclaration.getVarType().getType());

        return null;
    }

    @Override
    public Object visitMethodCall(final MethodCallExpression methodCallExpression, final Scope scope) {
        methodCallExpression.getTarget().ifPresent(it -> it.accept(this, scope));
        methodCallExpression.getArguments().forEach(it -> it.accept(this, scope));
        return null;
    }

    @Override
    public Object visitBinaryExpression(final BinaryExpression binaryExpression, final Scope scope) {
        binaryExpression.getLeft().accept(this, scope);
        binaryExpression.getRight().accept(this, scope);
        return null;
    }

    @Override
    public Object visitUnaryExpression(final UnaryExpression unaryExpression, final Scope scope) {
        unaryExpression.getExpression().accept(this, scope);
        return null;
    }

    @Override
    public Object visitExpressionStatement(final ExpressionStatement expressionStatement, final Scope scope) {
        expressionStatement.getExpression().accept(this, scope);
        return null;
    }

    @Override
    public Object visitBlockStatement(final BlockStatement blockStatement, final Scope scope) {
        blockStatement.getStatements().forEach(it -> it.accept(this, scope));
        return null;
    }

    @Override
    public Object visitFieldAccessExpression(final FieldAccessExpression fieldAccessExpression, final Scope scope) {
        fieldAccessExpression.getScope().accept(this, scope);
        return null;
    }

    @Override
    public Object visitReturnStatement(final ReturnStatement returnStatement, final Scope scope) {
        returnStatement.getExpression().accept(this, scope);
        return null;
    }

    @Override
    public Object visitAnnotatedType(final AnnotatedTypeExpression annotatedTypeExpression, final Scope scope) {
        annotatedTypeExpression.getIdentifier().accept(this, scope);
        annotatedTypeExpression.type(annotatedTypeExpression.getIdentifier().getType());
        return null;
    }

    @Override
    public Object visitIdentifierExpression(final IdentifierExpression identifierExpression, final Scope scope) {
        final String name = identifierExpression.getName();
        final var resolvedSymbol = scope.resolveSymbol(Name.of(name))
                .orElseGet(() -> (AbstractSymbol) elements.getTypeElement(name));

        if (resolvedSymbol != null) {
            identifierExpression.setSymbol(resolvedSymbol);
        }

        return null;
    }

    @Override
    public Object visitClassOrInterfaceTypeExpression(final ClassOrInterfaceTypeExpression classOrInterfaceTypeExpression, final Scope scope) {
        final var name = classOrInterfaceTypeExpression.getName();
        final var typeElement = classFinder.findClass(name, scope);
        final var typeArguments = classOrInterfaceTypeExpression.getTypeArguments();

        if (typeArguments != null) {
            typeArguments.forEach(typeArg -> typeArg.accept(this, scope));
        }

        if (typeElement != null) {
            final TypeMirror[] typeArgs;

            if (typeArguments != null) {
                typeArgs = typeArguments.stream()
                        .map(Tree::getType)
                        .toArray(TypeMirror[]::new);
            } else {
                typeArgs = new TypeMirror[0];
            }

            var type = types.getDeclaredType(typeElement, typeArgs);
            type = classOrInterfaceTypeExpression.isNullable()
                    ? type.asNullableType()
                    : type.asNonNullableType();
            classOrInterfaceTypeExpression.type(type);
        } else {
            final var qualifiedName =  name.getValue().toString();
            final var classSymbol = createErrorType(qualifiedName);
            classOrInterfaceTypeExpression.type(classSymbol.asType());
            reportError("cannot find symbol. Symbol: class " + name);
        }

        return null;
    }

    private ClassSymbol createErrorType(final String className) {
        final var qName = QualifiedName.from(className);

        final var packageSymbol = new PackageSymbol(
                qName.packageName(),
                null
        );

        final var classSymbol = new ClassSymbol(
                ElementKind.CLASS,
                qName.simpleName(),
                NestingKind.TOP_LEVEL,
                packageSymbol
        );

        final var type = new ErrorTypeImpl(classSymbol, true);
        classSymbol.setType(type);
        return classSymbol;
    }

    private void reportError(final String message) {
        diagnosticListener.report(new DefaultDiagnostic<>(Diagnostic.Kind.ERROR, message, UnknownSource.INSTANCE));
    }

    @Override
    public Object visitNoType(final NoTypeExpression noTypeExpression, final Scope scope) {
        final var type = types.getNoType(noTypeExpression.getKind());
        noTypeExpression.type(type);
        return null;
    }

    @Override
    public Object visitPrimitiveTypeExpression(final PrimitiveTypeExpression primitiveTypeExpression, final Scope scope) {
        final var type = types.getPrimitiveType(primitiveTypeExpression.getKind());
        primitiveTypeExpression.type(type);
        return null;
    }

    @Override
    public Object visitAnnotationExpression(final AnnotationExpression annotationExpression, final Scope scope) {
        annotationExpression.getAnnotationType().accept(this, scope);
        annotationExpression.type(annotationExpression.getAnnotationType().getType());
        annotationExpression.getArguments().values().forEach(value -> value.accept(this, scope));
        return null;
    }

    @Override
    public Object visitLiteralExpression(final LiteralExpression literalExpression, final Scope scope) {
        final var type = switch (literalExpression.getLiteralType()) {
            case NULL -> types.getNullType();
            case BOOLEAN -> types.getPrimitiveType(TypeKind.BOOLEAN);
            case BYTE -> types.getPrimitiveType(TypeKind.BYTE);
            case SHORT -> types.getPrimitiveType(TypeKind.SHORT);
            case INT -> types.getPrimitiveType(TypeKind.INT);
            case LONG -> types.getPrimitiveType(TypeKind.LONG);
            case FLOAT -> types.getPrimitiveType(TypeKind.FLOAT);
            case DOUBLE -> types.getPrimitiveType(TypeKind.DOUBLE);
            case STRING -> elements.getTypeElement("java.lang.String").asType();
            case CLASS -> {
                final var classLiteralExpression = (ClassLiteralExpression) literalExpression;

                if (classLiteralExpression.getClazz() instanceof PrimitiveTypeExpression primitiveTypeExpression) {
                    primitiveTypeExpression.accept(this, scope);
                    yield primitiveTypeExpression.getType();
                } else {
                    final var clazz = classLiteralExpression.getClazz();
                    final Name className;

                    if (clazz instanceof AnnotatedTypeExpression annotatedTypeExpression) {
                        className = ((ClassOrInterfaceTypeExpression)annotatedTypeExpression.getIdentifier()).getName();
                    } else {
                        className = ((ClassOrInterfaceTypeExpression)clazz).getName();
                    }

                    final var typeElement = elements.getTypeElement(className);

                    if (typeElement != null) {
                        yield typeElement.asType();
                    } else {
                        yield createErrorType(className.toString()).asType();
                    }
                }
            }
            case CHAR -> types.getPrimitiveType(TypeKind.CHAR);
        };
        literalExpression.type(type);

        return null;
    }

    @Override
    public Object visitArrayInitializerExpression(final ArrayInitializerExpression arrayInitializerExpression, final Scope scope) {
        arrayInitializerExpression.getValues().forEach(it -> it.accept(this, scope));
        return null;
    }

    @Override
    public Object visitNewClassExpression(final NewClassExpression newClassExpression, final Scope scope) {
        newClassExpression.getClazz().accept(this, scope);
        newClassExpression.getArguments().forEach(it -> it.accept(this, scope));
        newClassExpression.type(newClassExpression.getClazz().getType());
        return null;
    }

    @Override
    public Object visitIfStatement(final IfStatement ifStatement, final Scope scope) {
        ifStatement.getCondition().accept(this, scope);
        ifStatement.getBody().accept(this, scope);
        return null;
    }

    @Override
    public Object visitWildCardTypeExpression(final WildCardTypeExpression wildCardTypeExpression, final Scope scope) {
        wildCardTypeExpression.getTypeExpression().accept(this, scope);

        final TypeMirror extendsType;
        final TypeMirror superType;

        if (wildCardTypeExpression.getBoundKind() == BoundKind.EXTENDS) {
            extendsType = wildCardTypeExpression.getTypeExpression().getType();
            superType = null;
        } else {
            extendsType = null;
            superType = wildCardTypeExpression.getTypeExpression().getType();
        }

        final var wildCardType = types.getWildcardType(extendsType, superType);
        wildCardTypeExpression.type(wildCardType);
        return null;
    }

    @Override
    public Object visitVarTypeExpression(final VarTypeExpression varTypeExpression, final Scope scope) {
        return null;
    }

    @Override
    public Object visitArrayTypeExpresion(final ArrayTypeExpression arrayTypeExpression, final Scope scope) {
        arrayTypeExpression.getComponentTypeExpression().accept(this, scope);
        arrayTypeExpression.type(types.getArrayType(arrayTypeExpression.getComponentTypeExpression().getType()));
        return null;
    }
}
