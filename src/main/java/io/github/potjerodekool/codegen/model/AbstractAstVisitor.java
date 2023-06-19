package io.github.potjerodekool.codegen.model;

import io.github.potjerodekool.codegen.model.element.*;
import io.github.potjerodekool.codegen.model.symbol.MethodSymbol;
import io.github.potjerodekool.codegen.model.tree.TreeVisitor;
import io.github.potjerodekool.codegen.model.tree.expression.*;
import io.github.potjerodekool.codegen.model.tree.statement.*;
import io.github.potjerodekool.codegen.model.type.*;

import java.util.List;

public abstract class AbstractAstVisitor<R,P> implements ElementVisitor<R,P>,
        TypeVisitor<R,P>,
        TreeVisitor<R,P>,
        AnnotationValueVisitor<R,P> {

    @Override
    public R visit(final AnnotationValue av,
                   final P param) {
        return av.accept(this, param);
    }

    @Override
    public R visitBoolean(final boolean b,
                          final P param) {
        return null;
    }

    @Override
    public R visitByte(final byte b,
                       final P param) {
        return null;
    }

    @Override
    public R visitChar(final char c,
                       final P param) {
        return null;
    }

    @Override
    public R visitDouble(final double d,
                         final P param) {
        return null;
    }

    @Override
    public R visitFloat(final float f,
                        final P param) {
        return null;
    }

    @Override
    public R visitInt(final int i,
                      final P param) {
        return null;
    }

    @Override
    public R visitLong(final long i,
                       final P param) {
        return null;
    }

    @Override
    public R visitShort(final short s,
                        final P param) {
        return null;
    }

    @Override
    public R visitString(final String s,
                         final P param) {
        return null;
    }

    @Override
    public R visitType(final TypeMirror t,
                       final P param) {
        return null;
    }

    @Override
    public R visitEnumConstant(final VariableElement c,
                               final P param) {
        return null;
    }

    @Override
    public R visitAnnotation(final AnnotationMirror a,
                             final P param) {
        return null;
    }

    @Override
    public R visitArray(final List<? extends AnnotationValue> values,
                        final P param) {
        return null;
    }

    @Override
    public R visitUnknown(final AnnotationValue av,
                          final P param) {
        return null;
    }

    @Override
    public R visit(final Element e,
                   final P param) {
        return visitUnknown(e, param);
    }

    @Override
    public R visitPackage(final PackageElement e,
                          final P param) {
        return visitUnknown(e, param);
    }

    @Override
    public R visitType(final TypeElement e,
                       final P param) {
        e.getAnnotationMirrors().forEach(it -> it.accept(this, param));
        if (e.getSuperclass() != null) {
            e.getSuperclass().accept(this, param);
        }
        e.getInterfaces().forEach(it -> it.accept(this, param));

        e.getEnclosedElements().forEach(it -> it.accept(this, param));
        return visitUnknown(e, param);
    }

    @Override
    public R visitVariable(final VariableElement e,
                           final P param) {
        return visitUnknown(e, param);
    }

    @Override
    public R visitExecutable(final ExecutableElement e,
                             final P param) {
        e.getAnnotationMirrors().forEach(it -> it.accept(this, param));
        e.getTypeParameters().forEach(it -> it.accept(this, param));
        e.getReturnType().accept(this, param);
        e.getParameters().forEach(it -> it.accept(this, param));

        if (e instanceof MethodSymbol methodSymbol) {
            methodSymbol.getBody().ifPresent(it -> it.accept(this, param));
        }
        return null;
    }

    @Override
    public R visitTypeParameter(final TypeParameterElement e,
                                final P param) {
        return visitUnknown(e, param);
    }

    @Override
    public R visitUnknown(final Element e,
                          final P param) {
        return null;
    }

    @Override
    public R visitUnknown(final Expression expression,
                          final P param) {
        return null;
    }

    @Override
    public R visitBinaryExpression(final BinaryExpression binaryExpression,
                                   final P param) {
        binaryExpression.getLeft().accept(this, param);
        binaryExpression.getRight().accept(this, param);
        return null;
    }

    @Override
    public R visitNameExpression(final NameExpression nameExpression,
                                 final P param) {
        return visitUnknown(nameExpression, param);
    }

    @Override
    public R visitFieldAccessExpression(final FieldAccessExpression fieldAccessExpression,
                                        final P param) {
        fieldAccessExpression.getScope().accept(this, param);
        fieldAccessExpression.getField().accept(this, param);
        return null;
    }

    @Override
    public R visitLiteralExpression(final LiteralExpression literalExpression,
                                    final P param) {
        return visitUnknown(literalExpression, param);
    }

    @Override
    public R visitMethodCall(final MethodCallExpression methodCallExpression,
                             final P param) {
        methodCallExpression.getTarget().ifPresent(it -> it.accept(this, param));
        methodCallExpression.getArguments().forEach(it -> it.accept(this, param));
        return null;
    }

    @Override
    public R visitArrayInitializerExpression(final ArrayInitializerExpression arrayInitializerExpression,
                                             final P param) {
        arrayInitializerExpression.getValues().forEach(it -> it.accept(this, param));
        return null;
    }

    @Override
    public R visitNamedMethodArgumentExpression(final NamedMethodArgumentExpression namedMethodArgumentExpression,
                                                final P param) {
        namedMethodArgumentExpression.getArgument().accept(this, param);
        return null;
    }

    @Override
    public R visitArrayAccessExpression(final ArrayAccessExpression arrayAccessExpression,
                                        final P param) {
        arrayAccessExpression.getArrayExpression().accept(this, param);
        arrayAccessExpression.getIndexExpression().accept(this, param);
        return null;
    }

    @Override
    public R visitVariableDeclaration(final VariableDeclaration variableDeclaration, final P param) {
        variableDeclaration.getInitExpression().ifPresent(it -> it.accept(this, param));
        return null;
    }

    @Override
    public R visitNewClassExpression(final NewClassExpression newClassExpression,
                                     final P param) {
        newClassExpression.getClassType().accept(this, param);
        return null;
    }

    @Override
    public R visitUnknown(final Statement statement,
                          final P param) {
        return null;
    }

    @Override
    public R visitBlockStatement(final BlockStatement blockStatement,
                                 final P param) {
        blockStatement.getStatements().forEach(it -> it.accept(this, param));
        return null;
    }

    @Override
    public R visitExpressionStatement(final ExpressionStatement expressionStatement,
                                      final P param) {
        expressionStatement.getExpression().accept(this, param);
        return null;
    }

    @Override
    public R visitReturnStatement(final ReturnStatement returnStatement,
                                  final P param) {
        returnStatement.getExpression().accept(this, param);
        return null;
    }

    @Override
    public R visitIfStatement(final IfStatement ifStatement,
                              final P param) {
        ifStatement.getCondition().accept(this, param);
        ifStatement.getBody().accept(this, param);
        return null;
    }

    @Override
    public R visit(final TypeMirror t,
                   final P param) {
        return visitUnknown(t, param);
    }

    @Override
    public R visitPrimitive(final PrimitiveType t,
                            final P param) {
        return visitUnknown(t, param);
    }

    @Override
    public R visitNull(final NullType t,
                       final P param) {
        return visitUnknown(t, param);
    }

    @Override
    public R visitArray(final ArrayType t,
                        final P param) {
        return visitUnknown(t, param);
    }

    @Override
    public R visitDeclared(final DeclaredType t,
                           final P param) {
        t.getAnnotationMirrors().forEach(it -> it.accept(this, param));
        t.getTypeArguments().forEach(it -> it.accept(this, param));
        return null;
    }

    @Override
    public R visitError(final ErrorType t,
                        final P param) {
        return visitUnknown(t, param);
    }

    @Override
    public R visitTypeVariable(final TypeVariable t,
                               final P param) {
        t.getAnnotationMirrors().forEach(it -> it.accept(this, param));
        if (t.getUpperBound() != null) {
            t.getUpperBound().accept(this, param);
        }

        if (t.getLowerBound() != null) {
            t.getLowerBound().accept(this, param);
        }
        return null;
    }

    @Override
    public R visitWildcard(final WildcardType t,
                           final P param) {
        t.getAnnotationMirrors().forEach(it -> it.accept(this, param));
        t.getTypeArguments().forEach(it -> it.accept(this, param));
        if (t.getExtendsBound() != null) {
            t.getExtendsBound().accept(this, param);
        }

        if (t.getSuperBound() != null) {
            t.getSuperBound().accept(this, param);
        }
        return null;
    }

    @Override
    public R visitExecutable(final ExecutableType t,
                             final P param) {
        return visitUnknown(t, param);
    }

    @Override
    public R visitNoType(final NoType t,
                         final P param) {
        return visitUnknown(t, param);
    }

    @Override
    public R visitUnknown(final TypeMirror t,
                          final P param) {
        return null;
    }

    @Override
    public R visitUnion(final UnionType t,
                        final P param) {
        t.getAnnotationMirrors().forEach(it -> it.accept(this, param));
        t.getAlternatives().forEach(it -> it.accept(this, param));
        return null;
    }

    @Override
    public R visitIntersection(final IntersectionType t,
                               final P param) {
        t.getAnnotationMirrors().forEach(it -> it.accept(this, param));
        t.getBounds().forEach(it -> it.accept(this, param));
        return null;
    }

    @Override
    public R visitVarType(final VarTypeImpl varType,
                          final P param) {
        return null;
    }
}
