package io.github.potjerodekool.codegen.model.tree;

import io.github.potjerodekool.codegen.model.element.ElementKind;
import io.github.potjerodekool.codegen.model.element.Modifier;
import io.github.potjerodekool.codegen.model.element.Name;
import io.github.potjerodekool.codegen.model.symbol.MethodSymbol;
import io.github.potjerodekool.codegen.model.tree.expression.Expression;
import io.github.potjerodekool.codegen.model.tree.statement.ClassDeclaration;
import io.github.potjerodekool.codegen.model.tree.statement.VariableDeclaration;
import io.github.potjerodekool.codegen.model.tree.statement.BlockStatement;
import io.github.potjerodekool.codegen.model.tree.type.NoTypeExpression;
import io.github.potjerodekool.codegen.model.type.TypeKind;
import io.github.potjerodekool.codegen.model.type.TypeMirror;

import java.util.*;
import java.util.function.Consumer;

public class MethodDeclaration implements Tree, WithMetaData, ElementTree {

    private Name simpleName;

    private ElementKind kind;
    private final Set<Modifier> modifiers = new LinkedHashSet<>();

    private final List<Tree> enclosed = new ArrayList<>();
    private Tree enclosing;
    private final List<AnnotationExpression> annotations = new ArrayList<>();

    private Expression returnType;

    private final List<TypeParameter> typeParameters = new ArrayList<>();

    private final List<VariableDeclaration> parameters = new ArrayList<>();

    private BlockStatement body;

    private MethodSymbol methodSymbol;

    private final Map<String, Object> metaData = new HashMap<>();

    private TypeMirror type;

    public MethodDeclaration(final CharSequence simpleName,
                             final Expression returnType,
                             final List<TypeParameter> typeParameters,
                             final List<? extends VariableDeclaration> parameters, final BlockStatement body) {
        this.simpleName = Name.of(simpleName);
        this.returnType = returnType;
        this.typeParameters.addAll(typeParameters);
        this.parameters.addAll(parameters);
        this.body = body;
    }

    public MethodDeclaration() {
    }

    public static MethodDeclaration constructor(final ClassDeclaration classDeclaration) {
        final var constructor = new MethodDeclaration()
                .kind(ElementKind.CONSTRUCTOR)
                .simpleName(classDeclaration.getSimpleName())
                .returnType(new NoTypeExpression(TypeKind.VOID));

        classDeclaration.addEnclosed(constructor);
        constructor.setEnclosing(classDeclaration);
        return constructor;
    }

    public static MethodDeclaration primaryConstructor(final ClassDeclaration classDeclaration) {
        final var constructor = new MethodDeclaration()
                .kind(ElementKind.CONSTRUCTOR)
                .simpleName(classDeclaration.getSimpleName())
                .returnType(new NoTypeExpression(TypeKind.VOID));

        classDeclaration.setPrimaryConstructor(constructor);
        constructor.setEnclosing(classDeclaration);
        return constructor;
    }

    public static MethodDeclaration method() {
        return new MethodDeclaration()
                .kind(ElementKind.METHOD);
    }

    public Map<String, Object> getMetaData() {
        return metaData;
    }

    public MethodDeclaration metaData(final String key,
                                      final Object value) {
        this.metaData.put(key, value);
        return this;
    }

    public Name getSimpleName() {
        return simpleName;
    }

    public MethodDeclaration simpleName(final Name simpleName) {
        this.simpleName = simpleName;
        return this;
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

    public MethodDeclaration annotation(final String className) {
        annotation(new AnnotationExpression(className));
        return this;
    }

    public MethodDeclaration annotation(final AnnotationExpression annotationExpression) {
        this.annotations.add(annotationExpression);
        return this;
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

    public MethodDeclaration returnType(final Expression returnType) {
        this.returnType = returnType;
        return this;
    }

    public List<TypeParameter> getTypeParameters() {
        return typeParameters;
    }

    public MethodDeclaration typeParameters(final List<TypeParameter> typeParameters) {
        this.typeParameters.addAll(typeParameters);
        return this;
    }

    public List<VariableDeclaration> getParameters() {
        return parameters;
    }

    public MethodDeclaration parameter(final Consumer<VariableDeclaration> parameterBuilder) {
        final var parameter = new VariableDeclaration().kind(ElementKind.PARAMETER);
        parameterBuilder.accept(parameter);
        parameter(parameter);
        return this;
    }

    public MethodDeclaration parameter(final VariableDeclaration parameter) {
        this.parameters.add(parameter);
        return this;
    }

    public MethodDeclaration parameters(final VariableDeclaration... parameters) {
        for (final VariableDeclaration parameter : parameters) {
            parameter(parameter);
        }
        return this;
    }

    public MethodDeclaration parameters(final Collection<VariableDeclaration> parameters) {
        this.parameters.addAll(parameters);
        return this;
    }

    public Optional<BlockStatement> getBody() {
        return Optional.ofNullable(body);
    }

    public MethodDeclaration body(final BlockStatement body) {
        this.body = body;
        return this;
    }

    public MethodSymbol getMethodSymbol() {
        return methodSymbol;
    }

    public void methodSymbol(final MethodSymbol methodSymbol) {
        this.methodSymbol = methodSymbol;
    }

    public ElementKind getKind() {
        return this.kind;
    }

    public MethodDeclaration kind(final ElementKind kind) {
        this.kind = kind;
        return this;
    }

    public Set<Modifier> getModifiers() {
        return this.modifiers;
    }

    public MethodDeclaration modifier(final Modifier modifier) {
        this.modifiers.add(modifier);
        return this;
    }

    public MethodDeclaration modifiers(final Modifier... modifiers) {
        for (final Modifier modifier : modifiers) {
            modifier(modifier);
        }
        return this;
    }

    public MethodDeclaration modifiers(final Collection<Modifier> modifiers) {
        for (final Modifier modifier : modifiers) {
            modifier(modifier);
        }
        return this;
    }

    public MethodDeclaration removeModifier(final Modifier modifier) {
        this.modifiers.remove(modifier);
        return this;
    }

    public boolean hasModifier(final Modifier modifier) {
        return this.modifiers.contains(modifier);
    }

    @Override
    public <R, P> R accept(final TreeVisitor<R, P> visitor, final P param) {
        return visitor.visitMethodDeclaration(this, param);
    }

    @Override
    public TypeMirror getType() {
        return type;
    }

    public MethodDeclaration type(final TypeMirror type) {
        this.type = type;
        return this;
    }
}
