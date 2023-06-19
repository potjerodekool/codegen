package io.github.potjerodekool.codegen.model.tree;

import io.github.potjerodekool.codegen.model.element.ElementKind;
import io.github.potjerodekool.codegen.model.element.Modifier;
import io.github.potjerodekool.codegen.model.element.Name;
import io.github.potjerodekool.codegen.model.symbol.MethodSymbol;
import io.github.potjerodekool.codegen.model.tree.expression.Expression;
import io.github.potjerodekool.codegen.model.tree.statement.VariableDeclaration;
import io.github.potjerodekool.codegen.model.tree.statement.BlockStatement;
import io.github.potjerodekool.codegen.model.type.TypeMirror;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;

public class MethodDeclaration implements ElementTree {

    private final Name simpleName;
    private final ElementKind kind;

    private final Set<Modifier> modifiers = new HashSet<>();
    private final List<Tree> enclosed = new ArrayList<>();
    private Tree enclosing;
    private final List<AnnotationExpression> annotations = new ArrayList<>();

    private Expression returnType;

    private final List<TypeParameter> typeParameters;

    private final List<VariableDeclaration> parameters = new ArrayList<>();

    private @Nullable BlockStatement body;

    private MethodSymbol methodSymbol;

    private final Map<String, Object> metaData = new HashMap<>();

    public MethodDeclaration(final Name simpleName,
                             final ElementKind kind,
                             final Expression returnType,
                             final List<TypeParameter> typeParameters,
                             final List<VariableDeclaration> parameters, final @Nullable BlockStatement body) {
        this.simpleName = simpleName;
        this.kind = kind;
        this.returnType = returnType;
        this.typeParameters = typeParameters;
        this.parameters.addAll(parameters);
        this.body = body;
    }

    public Map<String, Object> getMetaData() {
        return metaData;
    }

    public Name getSimpleName() {
        return simpleName;
    }

    public ElementKind getKind() {
        return kind;
    }

    public void addModifier(final Modifier modifier) {
        this.modifiers.add(modifier);
    }

    public void addModifiers(final Modifier... modifiers) {
        addModifiers(List.of(modifiers));
    }

    public void addModifiers(final Collection<Modifier> modifiers) {
        this.modifiers.addAll(modifiers);
    }

    public Set<Modifier> getModifiers() {
        return modifiers;
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

    public void addAnnotation(final AnnotationExpression annotationExpression) {
        this.annotations.add(annotationExpression);
    }

    public Expression getReturnType() {
        return returnType;
    }

    public void setReturnType(final Expression returnType) {
        this.returnType = returnType;
    }

    public List<TypeParameter> getTypeParameters() {
        return typeParameters;
    }

    public List<VariableDeclaration> getParameters() {
        return parameters;
    }

    public void addParameter(final VariableDeclaration parameter) {
        this.parameters.add(parameter);
    }

    public Optional<BlockStatement> getBody() {
        return Optional.ofNullable(body);
    }

    public void setBody(final BlockStatement body) {
        this.body = body;
    }

    @Override
    public TypeMirror getType() {
        return null;
    }

    @Override
    public void setType(final TypeMirror type) {
    }

    @Override
    public <R, P> R accept(final TreeVisitor<R, P> visitor, final P param) {
        return visitor.visitMethodDeclaration(this, param);
    }

    public MethodSymbol getMethodSymbol() {
        return methodSymbol;
    }

    public void setMethodSymbol(final MethodSymbol methodSymbol) {
        this.methodSymbol = methodSymbol;
    }
}
