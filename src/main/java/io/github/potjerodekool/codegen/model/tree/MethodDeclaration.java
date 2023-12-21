package io.github.potjerodekool.codegen.model.tree;

import io.github.potjerodekool.codegen.model.element.ElementKind;
import io.github.potjerodekool.codegen.model.element.Modifier;
import io.github.potjerodekool.codegen.model.element.Name;
import io.github.potjerodekool.codegen.model.symbol.MethodSymbol;
import io.github.potjerodekool.codegen.model.tree.expression.Expression;
import io.github.potjerodekool.codegen.model.tree.statement.VariableDeclaration;
import io.github.potjerodekool.codegen.model.tree.statement.BlockStatement;

import java.util.*;

public abstract class MethodDeclaration<MD extends MethodDeclaration<MD>> implements Tree, WithMetaData {

    private Name simpleName;

    private final List<Tree> enclosed = new ArrayList<>();
    private Tree enclosing;
    private final List<AnnotationExpression> annotations = new ArrayList<>();

    private Expression returnType;

    private final List<TypeParameter> typeParameters = new ArrayList<>();

    private final List<VariableDeclaration<?>> parameters = new ArrayList<>();

    private BlockStatement body;

    private MethodSymbol methodSymbol;

    private final Map<String, Object> metaData = new HashMap<>();

    public MethodDeclaration(final CharSequence simpleName,
                             final Expression returnType,
                             final List<TypeParameter> typeParameters,
                             final List<? extends VariableDeclaration<?>> parameters, final BlockStatement body) {
        this.simpleName = Name.of(simpleName);
        this.returnType = returnType;
        this.typeParameters.addAll(typeParameters);
        this.parameters.addAll(parameters);
        this.body = body;
    }

    public MethodDeclaration() {
    }

    public Map<String, Object> getMetaData() {
        return metaData;
    }

    public Name getSimpleName() {
        return simpleName;
    }

    public MD setSimpleName(final Name simpleName) {
        this.simpleName = simpleName;
        return (MD) this;
    }

    public List<Tree> getEnclosed() {
        return enclosed;
    }

    public void addEnclosed(final Tree child) {
        this.enclosed.add(child);
    }

    public void addEnclosed(final Collection<Tree> childs) {
        this.enclosed.addAll(childs);
    }

    public Tree getEnclosing() {
        return enclosing;
    }

    public void setEnclosing(final Tree enclosing) {
        this.enclosing = enclosing;
    }

    public List<AnnotationExpression> getAnnotations() {
        return annotations;
    }

    public MD annotation(final String className) {
        annotation(new AnnotationExpression(className));
        return (MD) this;
    }
    public MD annotation(final AnnotationExpression annotationExpression) {
        this.annotations.add(annotationExpression);
        return (MD) this;
    }

    public void removeAnnotation(final AnnotationExpression annotationExpression) {
        this.annotations.remove(annotationExpression);
    }

    public AnnotationExpression getAnnotation(final String name) {
        return this.annotations.stream()
                .filter(annotationExpression -> annotationExpression.getAnnotationType().getName().contentEquals(name))
                .findFirst()
                .orElse(null);
    }

    public Expression getReturnType() {
        return returnType;
    }

    public MD setReturnType(final Expression returnType) {
        this.returnType = returnType;
        return (MD) this;
    }

    public List<TypeParameter> getTypeParameters() {
        return typeParameters;
    }

    public MD addTypeParameters(final List<TypeParameter> typeParameters) {
        this.typeParameters.addAll(typeParameters);
        return (MD) this;
    }

    public List<VariableDeclaration<?>> getParameters() {
        return parameters;
    }

    public MD addParameter(final VariableDeclaration<?> parameter) {
        this.parameters.add(parameter);
        return (MD) this;
    }

    public Optional<BlockStatement> getBody() {
        return Optional.ofNullable(body);
    }

    public MD setBody(final BlockStatement body) {
        this.body = body;
        return (MD) this;
    }

    public MethodSymbol getMethodSymbol() {
        return methodSymbol;
    }

    public void setMethodSymbol(final MethodSymbol methodSymbol) {
        this.methodSymbol = methodSymbol;
    }

    public abstract ElementKind getKind();

    public abstract Set<Modifier> getModifiers();

    @Override
    public <R, P> R accept(final TreeVisitor<R, P> visitor, final P param) {
        return visitor.visitMethodDeclaration(this, param);
    }
}
