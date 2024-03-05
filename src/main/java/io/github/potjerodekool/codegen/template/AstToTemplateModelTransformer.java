package io.github.potjerodekool.codegen.template;

import io.github.potjerodekool.codegen.model.CompilationUnit;
import io.github.potjerodekool.codegen.model.element.ElementKind;
import io.github.potjerodekool.codegen.model.tree.AnnotationExpression;
import io.github.potjerodekool.codegen.model.tree.MethodDeclaration;
import io.github.potjerodekool.codegen.model.tree.Tree;
import io.github.potjerodekool.codegen.model.tree.TreeVisitor;
import io.github.potjerodekool.codegen.model.tree.expression.*;
import io.github.potjerodekool.codegen.model.tree.statement.*;
import io.github.potjerodekool.codegen.model.tree.type.*;
import io.github.potjerodekool.codegen.model.type.*;
import io.github.potjerodekool.codegen.model.type.immutable.WildcardType;
import io.github.potjerodekool.codegen.template.model.TCompilationUnit;
import io.github.potjerodekool.codegen.template.model.annotation.Annot;
import io.github.potjerodekool.codegen.template.model.element.Elem;
import io.github.potjerodekool.codegen.template.model.element.MethodElem;
import io.github.potjerodekool.codegen.template.model.element.TypeElem;
import io.github.potjerodekool.codegen.template.model.element.VariableElem;
import io.github.potjerodekool.codegen.template.model.expression.*;
import io.github.potjerodekool.codegen.template.model.statement.*;
import io.github.potjerodekool.codegen.template.model.type.*;

public class AstToTemplateModelTransformer implements TreeVisitor<Object, Object>,
        TypeVisitor<Object, Object> {

    public TCompilationUnit transform(final CompilationUnit cu) {
        final var unit = new TCompilationUnit(cu.getLanguage());
        unit.packageName(cu.getPackageDeclaration().getName().getName());

        cu.getClassDeclarations().stream()
                .map(classDeclaration -> classDeclaration.accept(this, null))
                .map(it -> (TypeElem)it)
                .forEach(unit::element);

        return unit;
    }

    @Override
    public Object visitClassDeclaration(final ClassDeclaration classDeclaration, final Object param) {
        final var typeElement = new TypeElem()
                .kind(classDeclaration.getKind())
                .modifiers(classDeclaration.getModifiers())
                .simpleName(classDeclaration.getSimpleName().toString());

        final var interfaces = classDeclaration.getImplementing().stream()
                .map(typeExpr -> (ClassOrInterfaceTypeExpr) typeExpr.accept(this, param))
                .toList();

        if (classDeclaration.getKind() == ElementKind.CLASS) {
            interfaces.forEach(typeElement::implement);
        }

        classDeclaration.getAnnotations().stream()
                        .map(annotationExpression -> (Annot) annotationExpression.accept(this, param))
                        .forEach(typeElement::annotation);

        classDeclaration.getEnclosed().stream()
                .map(e -> (Elem<?>) e.accept(this, param))
                .forEach(typeElement::enclosedElement);
        return typeElement;
    }

    @Override
    public Object visitMethodDeclaration(final MethodDeclaration methodDeclaration, final Object param) {
        final var methodElement = new MethodElem()
                .kind(methodDeclaration.getKind())
                .modifiers(methodDeclaration.getModifiers())
                .simpleName(methodDeclaration.getSimpleName().toString())
                .returnType((TypeExpr) methodDeclaration.getReturnType().accept(this, param));

        methodElement.annotations(
                methodDeclaration.getAnnotations().stream()
                        .map(annotationExpression -> (Annot) annotationExpression.accept(this, param))
                        .toList()
        );

        methodDeclaration.getParameters().stream()
                .map(methodParam -> (VariableElem) methodParam.accept(this, param))
                .forEach(methodElement::parameter );

        methodDeclaration.getBody().ifPresent(body -> {
            final var methodBody = (BlockStm) body.accept(this, param);
            methodElement.body(methodBody);
        });

        return methodElement;
    }

    @Override
    public Object visitVariableDeclaration(final VariableDeclaration variableDeclaration, final Object param) {
        final var type = (TypeExpr) variableDeclaration.getVarType().accept(this, param);
        final var initExpr = variableDeclaration.getInitExpression()
                .map(initExpression -> (Expr) initExpression.accept(this, param))
                .orElse(null);

        final var annotations = variableDeclaration.getAnnotations().stream()
                .map(annotationExpression -> (Annot) annotationExpression.accept(this, param))
                .toList();

        if (variableDeclaration.getKind() == ElementKind.LOCAL_VARIABLE) {
            return new VariableDeclarationStm()
                    .modifiers(variableDeclaration.getModifiers())
                    .type(type)
                    .identifier(variableDeclaration.getName())
                    .initExpression(initExpr);
        } else {
            final var variable = new VariableElem()
                    .kind(variableDeclaration.getKind())
                    .modifiers(variableDeclaration.getModifiers())
                    .type(type)
                    .simpleName(variableDeclaration.getName())
                    .initExpression(initExpr);

            annotations.forEach(variable::annotation);

            return variable;
        }
    }

    @Override
    public Object visitClassOrInterfaceTypeExpression(final ClassOrInterfaceTypeExpression classOrInterfaceTypeExpression, final Object param) {
        final var clasType = new ClassOrInterfaceTypeExpr(classOrInterfaceTypeExpression.getName().toString());

        if (classOrInterfaceTypeExpression.getTypeArguments() != null) {
            classOrInterfaceTypeExpression.getTypeArguments().stream()
                    .map(typeExpr -> (TypeExpr) typeExpr.accept(this, param))
                    .forEach(clasType::typeArgument);
        }

        return clasType;
    }

    @Override
    public Object visitNoType(final NoTypeExpression noTypeExpression, final Object param) {
        if (noTypeExpression.getKind() == TypeKind.VOID) {
            return NoTypeExpr.createVoidType();
        } else {
            throw new UnsupportedOperationException("Unsupported type: " + noTypeExpression.getKind());
        }
    }

    @Override
    public Object visitAnnotationExpression(final AnnotationExpression annotationExpression, final Object param) {
        final var annot = new Annot(annotationExpression.getAnnotationType().getName().toString());

        annotationExpression.getArguments().forEach((name, value) -> {
            final var annotValue = (Expr) value.accept(this, param);
            annot.value(name, annotValue);
        });

        return annot;
    }

    @Override
    public Object visitLiteralExpression(final LiteralExpression literalExpression, final Object param) {
        if (literalExpression instanceof StringValueLiteralExpression stringValueLiteralExpression) {
            return createLiteralExpression(stringValueLiteralExpression);
        } else if (literalExpression instanceof ClassLiteralExpression classLiteralExpression) {
            return createClassLiteralExp(classLiteralExpression);
        } else {
            throw new UnsupportedOperationException("Unsupported literal type: " + literalExpression.getClass());
        }
    }

    private SimpleLiteralExpr createLiteralExpression(final StringValueLiteralExpression stringValueLiteralExpression) {
        final var value = switch (stringValueLiteralExpression.getLiteralType()) {
            case NULL -> null;
            case BOOLEAN -> Boolean.parseBoolean(stringValueLiteralExpression.getValue());
            case BYTE -> Byte.parseByte(stringValueLiteralExpression.getValue());
            case SHORT -> Short.parseShort(stringValueLiteralExpression.getValue());
            case INT -> Integer.parseInt(stringValueLiteralExpression.getValue());
            case LONG -> Long.parseLong(stringValueLiteralExpression.getValue());
            case FLOAT -> Float.parseFloat(stringValueLiteralExpression.getValue());
            case DOUBLE -> Double.parseDouble(stringValueLiteralExpression.getValue());
            case STRING -> stringValueLiteralExpression.getValue();
            case CHAR -> stringValueLiteralExpression.getValue().charAt(0);
            default -> throw new UnsupportedOperationException("Unsupported literal type: " + stringValueLiteralExpression.getLiteralType());
        };

        return new SimpleLiteralExpr(value);
    }

    private ClassLiteralExpr createClassLiteralExp(final ClassLiteralExpression classLiteralExpression) {
        final var clazz = classLiteralExpression.getClazz();
        final String className;

        if (clazz instanceof ClassOrInterfaceTypeExpression classOrInterfaceTypeExpression) {
            className = classOrInterfaceTypeExpression.getName().toString();
        } else if (clazz instanceof PrimitiveTypeExpression primitiveTypeExpression) {
            className = getPrimitiveTypeName((PrimitiveType) primitiveTypeExpression.getType());
        } else {
            throw new UnsupportedOperationException();
        }
        return new ClassLiteralExpr(className);
    }


    @Override
    public Object visitBlockStatement(final BlockStatement blockStatement, final Object param) {
        final var statements = blockStatement.getStatements().stream()
                .map(statement -> (Stm) statement.accept(this, param))
                .toList();
        return new BlockStm(statements);
    }

    @Override
    public Object visitMethodCall(final MethodCallExpression methodCallExpression, final Object param) {
        final var targetExp = methodCallExpression.getTarget()
                .map(target -> (Expr) target.accept(this, param))
                .orElse(null);

        final var arguments = methodCallExpression.getArguments().stream()
                .map(argument -> (Expr) argument.accept(this, param))
                .toList();

        return new MethodInvocationExpr()
                .target(targetExp)
                .name(methodCallExpression.getMethodName().getName())
                .arguments(arguments);
    }

    @Override
    public Object visitIdentifierExpression(final IdentifierExpression identifierExpression, final Object param) {
        return new IdentifierExpr(identifierExpression.getName());
    }

    @Override
    public Object visitIfStatement(final IfStatement ifStatement, final Object param) {
        final var condition = (Expr) ifStatement.getCondition().accept(this, param);
        final var thenStatement = (Stm) ifStatement.getBody().accept(this, param);

        return new IfStm()
                .condition(condition)
                .thenStatement(thenStatement)
                .elseStatement(null);
    }

    @Override
    public Object visitUnaryExpression(final UnaryExpression unaryExpression, final Object param) {
        return new UnaryExpr(
                unaryExpression.getOperator(),
                (Expr) unaryExpression.getExpression().accept(this, param)
        );
    }

    @Override
    public Object visitBinaryExpression(final BinaryExpression binaryExpression, final Object param) {
        return new BinaryExpr()
                .left((Expr) binaryExpression.getLeft().accept(this, param))
                .operator(binaryExpression.getOperator())
                .right((Expr) binaryExpression.getRight().accept(this, param));
    }

    @Override
    public Object visitExpressionStatement(final ExpressionStatement expressionStatement, final Object param) {
        return new ExpressionStm((Expr) expressionStatement.getExpression().accept(this, param));
    }

    @Override
    public Object visitReturnStatement(final ReturnStatement returnStatement, final Object param) {
        return new ReturnStm((Expr) returnStatement.getExpression().accept(this, param));
    }

    @Override
    public Object visitPropertyAccessExpression(final PropertyAccessExpression propertyAccessExpression, final Object param) {
        final var target = (Expr) propertyAccessExpression.getTarget().accept(this, param);
        return new PropertyAccessExpr()
                .target(target)
                .name(propertyAccessExpression.getName().getName());
    }

    @Override
    public Object visitArrayInitializerExpression(final ArrayInitializerExpression arrayInitializerExpression, final Object param) {
        final var values = arrayInitializerExpression.getValues().stream()
                .map(value -> (Expr) value.accept(this, param))
                .toList();
        return new ArrayExpr()
                .values(values);
    }

    @Override
    public Object visitFieldAccessExpression(final FieldAccessExpression fieldAccessExpression, final Object param) {
        final var target = (Expr) accept(fieldAccessExpression.getScope(), param);
        final var field = new IdentifierExpr(fieldAccessExpression.getField().toString());
        return new FieldAccessExpr()
                .target(target)
                .field(field);
    }

    @Override
    public Object visitArrayTypeExpresion(final ArrayTypeExpression arrayTypeExpression, final Object param) {
        final var type = (ClassType) ((ArrayType) arrayTypeExpression.getType()).getComponentType();
        final TypeExpr componentType = (TypeExpr) type.accept(this, param);
        return new ArrayTypeExpr(componentType);
    }

    @Override
    public Object visitPrimitiveTypeExpression(final PrimitiveTypeExpression primitiveTypeExpression, final Object param) {
        final var type = (PrimitiveType) primitiveTypeExpression.getType();
        return new PrimitiveTypeExpr(type.getKind());
    }

    private String getPrimitiveTypeName(final PrimitiveType type) {
        return switch (type.getKind()) {
            case BOOLEAN -> "boolean";
            case INT -> "int";
            case BYTE -> "byte";
            case SHORT -> "short";
            case LONG -> "long";
            case CHAR -> "char";
            case FLOAT -> "float";
            case DOUBLE -> "double";
            default -> throw new UnsupportedOperationException("Unsupported primitive type: " + type.getKind());
        };
    }

    @Override
    public Object visitNewClassExpression(final NewClassExpression newClassExpression, final Object param) {
        final var type = (ClassType) newClassExpression.getClazz().getType();
        final var className = type.asElement().getQualifiedName();
        final var arguments = newClassExpression.getArguments().stream()
                .map(argument -> (Expr) argument.accept(this, param))
                .toList();

        return new NewClassExpr()
                .name(className.toString())
                .arguments(arguments);
    }

    @Override
    public Object visitWildCardTypeExpression(final WildCardTypeExpression wildCardTypeExpression, final Object param) {
        final var expr = (Expr) wildCardTypeExpression.getTypeExpression().accept(this, param);

        return new WildCardTypeExpr()
                .boundKind(wildCardTypeExpression.getBoundKind())
                .expr(expr);
    }

    @Override
    public Object visitVarTypeExpression(final VarTypeExpression varTypeExpression, final Object param) {
        return new VarTypeExp();
    }

    private <T> T accept(final Tree tree, final Object param) {
        return tree != null
                ? (T) tree.accept(this, param)
                : null;
    }

    @Override
    public Object visit(final TypeMirror t, final Object param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitPrimitive(final PrimitiveType t, final Object param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitNull(final NullType t, final Object param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitArray(final ArrayType t, final Object param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitDeclared(final DeclaredType t, final Object param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitError(final ErrorType t, final Object param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitTypeVariable(final TypeVariable t, final Object param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitWildcard(final WildcardType t, final Object param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitExecutable(final ExecutableType t, final Object param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitNoType(final NoType t, final Object param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitUnknown(final TypeMirror t, final Object param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitUnion(final UnionType t, final Object param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitIntersection(final IntersectionType t, final Object param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitVarType(final VarType varType, final Object object) {
        throw new UnsupportedOperationException();
    }
}
