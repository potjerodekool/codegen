package io.github.potjerodekool.codegen.resolve;

import io.github.potjerodekool.codegen.model.Attribute;
import io.github.potjerodekool.codegen.model.CompilationUnit;
import io.github.potjerodekool.codegen.model.tree.AnnotationExpression;
import io.github.potjerodekool.codegen.model.tree.MethodDeclaration;
import io.github.potjerodekool.codegen.model.tree.expression.*;
import io.github.potjerodekool.codegen.model.element.*;
import io.github.potjerodekool.codegen.model.symbol.*;
import io.github.potjerodekool.codegen.model.tree.TreeVisitor;
import io.github.potjerodekool.codegen.model.tree.type.AnnotatedTypeExpression;
import io.github.potjerodekool.codegen.model.tree.type.NoTypeExpression;
import io.github.potjerodekool.codegen.model.tree.type.ParameterizedType;
import io.github.potjerodekool.codegen.model.tree.type.PrimitiveTypeExpression;
import io.github.potjerodekool.codegen.model.tree.statement.*;
import io.github.potjerodekool.codegen.model.type.*;
import io.github.potjerodekool.codegen.model.util.Elements;
import io.github.potjerodekool.codegen.model.util.QualifiedName;

import java.util.List;

public class ImportOrganiser implements ElementVisitor<Void, CompilationUnit>,
        TypeVisitor<Void, CompilationUnit>,
        TreeVisitor<Void, CompilationUnit>,
        AnnotationValueVisitor<Void, CompilationUnit> {

    public void organiseImports(final CompilationUnit compilationUnit) {
        compilationUnit.getPackageElement().accept(this, compilationUnit);
        compilationUnit.getClassDeclarations().forEach(classDeclaration -> classDeclaration.accept(this, compilationUnit));
    }

    @Override
    public Void visitPackage(final PackageElement packageElement,
                             final CompilationUnit cu) {
        return null;
    }

    @Override
    public Void visitType(final TypeElement typeElement,
                          final CompilationUnit compilationUnit) {
        processAnnotations(typeElement.getAnnotationMirrors(), compilationUnit);

        if (typeElement.getSuperclass() != null) {
            typeElement.getSuperclass().accept(this, compilationUnit);
        }

        typeElement.getInterfaces().forEach(it -> it.accept(this, compilationUnit));

        final var primaryConstructor = ((ClassSymbol) typeElement).getPrimaryConstructor();

        if (primaryConstructor != null) {
            primaryConstructor.accept(this, compilationUnit);
        }

        typeElement.getEnclosedElements().forEach(it -> it.accept(this, compilationUnit));

        return null;
    }

    @Override
    public Void visitVariable(final VariableElement variableElement,
                              final CompilationUnit cu) {
        /*
        Expression initExpression = ((VariableSymbol) variableElement).getInitExpression();

        if (initExpression != null) {
            initExpression.accept(this, cu);
        }
         */

        processAnnotations(variableElement.getAnnotationMirrors(), cu);
        return null;
    }

    @Override
    public Void visitExecutable(final ExecutableElement methodElement,
                                         final CompilationUnit cu) {
        processAnnotations(methodElement.getAnnotationMirrors(), cu);
        methodElement.getParameters().forEach(it -> it.accept(this, cu));

        methodElement.getReturnType().accept(this, cu);
        ((MethodSymbol)methodElement).getBody().ifPresent(it -> it.accept(this, cu));

        return null;
    }

    @Override
    public Void visitUnknown(final Element element,
                             final CompilationUnit cu) {
        return null;
    }

    //Expressions
    @Override
    public Void visitUnknown(final Expression expression,
                             final CompilationUnit cu) {
        return null;
    }

    @Override
    public Void visitBinaryExpression(final BinaryExpression binaryExpression,
                                      final CompilationUnit cu) {
        binaryExpression.getLeft().accept(this, cu);
        binaryExpression.getRight().accept(this, cu);
        return null;
    }

    @Override
    public Void visitFieldAccessExpression(final FieldAccessExpression fieldAccessExpression,
                                           final CompilationUnit cu) {
        fieldAccessExpression.getScope().accept(this, cu);
        fieldAccessExpression.getField().accept(this, cu);
        return null;
    }

    @Override
    public Void visitNameExpression(final NameExpression nameExpression,
                                    final CompilationUnit cu) {
        final var symbol = nameExpression.getSymbol();

        if (symbol instanceof ClassSymbol classSymbol) {
            importClass(classSymbol.getQualifiedName(), cu);
        }

        return null;
    }

    @Override
    public Void visitMethodCall(final MethodCallExpression methodCallExpression,
                                final CompilationUnit cu) {
        methodCallExpression.getTarget().ifPresent(it -> it.accept(this, cu));
        methodCallExpression.getArguments().forEach(arg -> arg.accept(this, cu));
        return null;
    }

    @Override
    public Void visitVariableDeclaration(final VariableDeclaration variableDeclaration,
                                         final CompilationUnit cu) {
        variableDeclaration.getInitExpression().ifPresent(it -> it.accept(this, cu));

        if (variableDeclaration.getType() != null) {
            variableDeclaration.getType().accept(this, cu);
        }
        return null;
    }

    @Override
    public Void visitClassDeclaration(final ClassDeclaration classDeclaration, final CompilationUnit compilationUnit) {
        classDeclaration.getEnclosed().forEach(enclosed -> enclosed.accept(this, compilationUnit));
        return null;
    }

    @Override
    public Void visitLiteralExpression(final LiteralExpression literalExpression,
                                       final CompilationUnit cu) {
        if (literalExpression.getLiteralType() == LiteralType.CLASS) {
            var type = literalExpression.getType();

            if (type instanceof DeclaredType declaredType) {
                final var className = Elements.getQualifiedName(declaredType.asElement());
                importClass(className, cu);
            }
        }

        return null;
    }

    @Override
    public Void visitNamedMethodArgumentExpression(final NamedMethodArgumentExpression namedMethodArgumentExpression,
                                                   final CompilationUnit cu) {
        namedMethodArgumentExpression.getArgument().accept(this, cu);
        return null;
    }

    @Override
    public Void visitAnnotation(final AnnotationMirror annotation,
                                final CompilationUnit cu) {
        importClass(Elements.getQualifiedName(annotation.getAnnotationType().asElement()), cu);
        annotation.getElementValues().values()
                .forEach(expression -> expression.accept(this, cu));
        return null;
    }

    @Override
    public Void visitArrayAccessExpression(final ArrayAccessExpression arrayAccessExpression,
                                           final CompilationUnit cu) {
        return null;
    }

    @Override
    public Void visitArrayInitializerExpression(final ArrayInitializerExpression arrayInitializerExpression,
                                                final CompilationUnit cu) {
        arrayInitializerExpression.getValues().forEach(it -> it.accept(this, cu));
        return null;
    }

    @Override
    public Void visitNewClassExpression(final NewClassExpression newClassExpression,
                                        final CompilationUnit cu) {
        newClassExpression.getClassType().accept(this, cu);
        return null;
    }

    //Statements
    @Override
    public Void visitUnknown(final Statement statement,
                             final CompilationUnit cu) {
        return null;
    }

    @Override
    public Void visitBlockStatement(final BlockStatement blockStatement,
                                    final CompilationUnit cu) {
        blockStatement.getStatements().forEach(it -> it.accept(this, cu));
        return null;
    }

    @Override
    public Void visitExpressionStatement(final ExpressionStatement expressionStatement,
                                         final CompilationUnit cu) {
        expressionStatement.getExpression().accept(this, cu);
        return null;
    }

    @Override
    public Void visitReturnStatement(final ReturnStatement returnStatement,
                                     final CompilationUnit cu) {
        returnStatement.getExpression().accept(this, cu);
        return null;
    }

    @Override
    public Void visitIfStatement(final IfStatement ifStatement,
                                 final CompilationUnit cu) {
        ifStatement.getCondition().accept(this, cu);
        ifStatement.getBody().accept(this, cu);
        return null;
    }

    @Override
    public Void visitDeclared(final DeclaredType declaredType,
                              final CompilationUnit compilationUnit) {
        declaredType.getTypeArguments().forEach(it -> it.accept(this, compilationUnit));
        declaredType.getAnnotationMirrors().forEach(annotation -> ((Attribute)annotation).accept(this, compilationUnit));

        importClass(Elements.getQualifiedName(declaredType.asElement()), compilationUnit);
        return null;
    }

    @Override
    public Void visitPrimitive(final PrimitiveType t,
                               final CompilationUnit compilationUnit) {
        return null;
    }

    @Override
    public Void visitArray(final ArrayType t,
                           final CompilationUnit compilationUnit) {
        t.getComponentType().accept(this, compilationUnit);
        return null;
    }

    @Override
    public Void visitExecutable(final ExecutableType executableType,
                                final CompilationUnit cu) {
        return null;
    }

    @Override
    public Void visitWildcard(final WildcardType wildcardType,
                              final CompilationUnit cu) {
        if (wildcardType.getExtendsBound() != null) {
            wildcardType.getExtendsBound().accept(this, cu);
        } else if (wildcardType.getSuperBound() != null) {
            wildcardType.getSuperBound().accept(this, cu);
        }
        return null;
    }

    @Override
    public Void visitUnknown(final TypeMirror type,
                             final CompilationUnit cu) {
        return null;
    }

    private void processAnnotations(final List<? extends AnnotationMirror> annotations,
                                    final CompilationUnit compilationUnit) {
        annotations.forEach(annotation -> processAnnotation(annotation, compilationUnit));
    }

    private void processAnnotation(final AnnotationMirror annotation,
                                               final CompilationUnit compilationUnit) {
        final Name className = Elements.getQualifiedName(annotation.getAnnotationType().asElement());
        importClass(className, compilationUnit);
        annotation.getElementValues().values().forEach(value -> value.accept(this, compilationUnit));
    }

    private void importClass(final String classname,
                             final CompilationUnit compilationUnit) {
        importClass(Name.of(classname), compilationUnit);
    }

    private void importClass(final Name classname,
                             final CompilationUnit compilationUnit) {
        if (!classname.toString().contains(".")) {
            return;
        }

        final var qualifiedName = QualifiedName.from(classname);

        if (qualifiedName.packageName().contentEquals("java.lang")
                || qualifiedName.packageName().contentEquals("kotlin")) {
            return;
        }

        if (matchesPackage(qualifiedName.toString(), compilationUnit)) {
            return;
        }

        final var imports = compilationUnit.getImports();

        final var simpleName = QualifiedName.from(classname).simpleName();
        final var simpleNameWithDot = "." + simpleName;

        for (final var anImport : imports) {
            if (classname.equals(anImport)) {
                return;
            } else if (anImport.toString().endsWith(simpleNameWithDot)) {
                return;
            }
        }

        if (imports.stream()
                .noneMatch(importStr -> classname.equals(importStr) || importStr.toString().endsWith(simpleNameWithDot))) {
            compilationUnit.addImport(classname);
        }
    }

    private boolean matchesPackage(final String classname,
                                   final CompilationUnit compilationUnit) {
        final var qualifiedName = QualifiedName.from(classname);
        final var packageElement = compilationUnit.getPackageElement();
        final var packageName = packageElement.getQualifiedName();
        return qualifiedName.packageName().equals(packageName);
    }

    @Override
    public Void visit(final AnnotationValue av, final CompilationUnit compilationUnit) {
        return null;
    }

    @Override
    public Void visitBoolean(final boolean value, final CompilationUnit param) {
        return null;
    }

    @Override
    public Void visitByte(final byte value, final CompilationUnit compilationUnit) {
        return null;
    }

    @Override
    public Void visitChar(final char value, final CompilationUnit param) {
        return null;
    }

    @Override
    public Void visitShort(final short value, final CompilationUnit param) {
        return null;
    }

    @Override
    public Void visitInt(final int value, final CompilationUnit param) {
        return null;
    }

    @Override
    public Void visitLong(final long value, final CompilationUnit param) {
        return null;
    }

    @Override
    public Void visitFloat(final float value, final CompilationUnit param) {
        return null;
    }

    @Override
    public Void visitDouble(final double value, final CompilationUnit param) {
        return null;
    }

    @Override
    public Void visitString(final String value, final CompilationUnit param) {
        return null;
    }

    @Override
    public Void visitArray(final List<? extends AnnotationValue> array, final CompilationUnit param) {
        return null;
    }

    @Override
    public Void visitUnknown(final AnnotationValue av, final CompilationUnit compilationUnit) {
        return null;
    }

    @Override
    public Void visitEnumConstant(final VariableElement variableElement, final CompilationUnit compilationUnit) {
        importClass(Elements.getQualifiedName(variableElement.getEnclosingElement()), compilationUnit);
        return null;
    }

    @Override
    public Void visitType(final TypeMirror classType, final CompilationUnit param) {
        return null;
    }

    @Override
    public Void visit(final Element e, final CompilationUnit compilationUnit) {
        return null;
    }

    @Override
    public Void visitTypeParameter(final TypeParameterElement e, final CompilationUnit compilationUnit) {
        return null;
    }

    @Override
    public Void visit(final TypeMirror t, final CompilationUnit compilationUnit) {
        return null;
    }

    @Override
    public Void visitNull(final NullType t, final CompilationUnit compilationUnit) {
        return null;
    }

    @Override
    public Void visitError(final ErrorType t, final CompilationUnit compilationUnit) {
        return null;
    }

    @Override
    public Void visitTypeVariable(final TypeVariable t, final CompilationUnit compilationUnit) {
        return null;
    }

    @Override
    public Void visitNoType(final NoType t, final CompilationUnit compilationUnit) {
        return null;
    }

    @Override
    public Void visitIntersection(final IntersectionType t, final CompilationUnit compilationUnit) {
        return null;
    }

    @Override
    public Void visitUnion(final UnionType t, final CompilationUnit compilationUnit) {
        return null;
    }

    @Override
    public Void visitVarType(final VarTypeImpl varType, final CompilationUnit compilationUnit) {
        return null;
    }

    @Override
    public Void visitAnnotatedType(final AnnotatedTypeExpression annotatedTypeExpression, final CompilationUnit compilationUnit) {
        return null;
    }

    @Override
    public Void visitAnnotationExpression(final AnnotationExpression annotationExpression, final CompilationUnit compilationUnit) {
        final var className = ((NameExpression) annotationExpression.getAnnotationType().getClazz()).getName();
        importClass(className, compilationUnit);
        annotationExpression.getArguments().values().forEach(arg -> arg.accept(this, compilationUnit));
        return null;
    }

    @Override
    public Void visitPrimitiveTypeExpression(final PrimitiveTypeExpression primitiveTypeExpression, final CompilationUnit param) {
        return null;
    }

    @Override
    public Void visitMethodDeclaration(final MethodDeclaration methodDeclaration, final CompilationUnit compilationUnit) {
        methodDeclaration.getTypeParameters().forEach(typeParameter -> typeParameter.accept(this, compilationUnit));
        methodDeclaration.getAnnotations().forEach(annotationExpression -> annotationExpression.accept(this, compilationUnit));
        methodDeclaration.getParameters().forEach(parameter -> parameter.accept(this, compilationUnit));
        methodDeclaration.getReturnType().accept(this, compilationUnit);

        return null;
    }

    @Override
    public Void visitNoType(final NoTypeExpression noTypeExpression, final CompilationUnit param) {
        return null;
    }

    @Override
    public Void visitParameterizedType(final ParameterizedType parameterizedType, final CompilationUnit param) {
        parameterizedType.getClazz().accept(this, param);
        parameterizedType.getType().accept(this, param);

        return null;
    }

}
