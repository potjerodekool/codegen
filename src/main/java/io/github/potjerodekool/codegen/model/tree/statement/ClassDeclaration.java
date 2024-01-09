package io.github.potjerodekool.codegen.model.tree.statement;

import io.github.potjerodekool.codegen.model.element.ElementKind;
import io.github.potjerodekool.codegen.model.element.Modifier;
import io.github.potjerodekool.codegen.model.element.Name;
import io.github.potjerodekool.codegen.model.symbol.ClassSymbol;
import io.github.potjerodekool.codegen.model.tree.*;
import io.github.potjerodekool.codegen.model.tree.expression.Expression;
import io.github.potjerodekool.codegen.model.tree.type.NoTypeExpression;
import io.github.potjerodekool.codegen.model.type.TypeKind;

import java.util.*;
import java.util.function.Consumer;

public class ClassDeclaration extends AbstractStatement implements WithMetaData {

    private Name simpleName;
    private ElementKind kind;
    private final Set<Modifier> modifiers = new LinkedHashSet<>();

    private final List<Tree> enclosed = new ArrayList<>();
    private Tree enclosing;
    private final List<AnnotationExpression> annotations = new ArrayList<>();

    private MethodDeclaration primaryConstructor;

    private Expression extending;

    private final List<Expression> implementing = new ArrayList<>();

    private final Map<String, Object> metaData = new HashMap<>();

    private ClassSymbol classSymbol;

    public ClassDeclaration() {
    }

    public ClassDeclaration(final CharSequence simpleName) {
        this.simpleName = Name.of(simpleName);
    }

    @Override
    public Map<String, Object> getMetaData() {
        return metaData;
    }

    public Name getSimpleName() {
        return simpleName;
    }

    public ClassDeclaration simpleName(final Name simpleName) {
        this.simpleName = simpleName;
        return this;
    }

    public Name getQualifiedName() {
        return qualifiedNameOf(this);
    }

    private Name qualifiedNameOf(final Tree tree) {
        if (tree instanceof ClassDeclaration classDeclaration) {
            final var enclosing = getEnclosing();

            if (enclosing == null) {
                return classDeclaration.getSimpleName();
            } else {
                final var enclosingName = qualifiedNameOf(enclosing);
                return Name.of(enclosingName, classDeclaration.simpleName);
            }
        } else if (tree instanceof PackageDeclaration packageDeclaration) {
            return Name.of(packageDeclaration.getName().getName());
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public ElementKind getKind() {
        return this.kind;
    }

    public ClassDeclaration kind(final ElementKind kind) {
        this.kind = kind;
        return this;
    }

    public Set<Modifier> getModifiers() {
        return this.modifiers;
    }

    public ClassDeclaration modifier(final Modifier modifier) {
        this.modifiers.add(modifier);
        return this;
    }

    public ClassDeclaration modifiers(final Modifier... modifiers) {
        for (final Modifier modifier : modifiers) {
            modifier(modifier);
        }
        return this;
    }

    public ClassDeclaration modifiers(final Collection<Modifier> modifiers) {
        for (final Modifier modifier : modifiers) {
            modifier(modifier);
        }
        return this;
    }

    public ClassDeclaration removeModifier(final Modifier modifier) {
        this.modifiers.remove(modifier);
        return this;
    }

    public List<Tree> getEnclosed() {
        return enclosed;
    }

    public void removeEnclosed(final Tree tree) {
        this.enclosed.remove(tree);
    }

    public ClassDeclaration constructor(final Consumer<MethodDeclaration> methodBuilder) {
        final var method = createConstructor();
        methodBuilder.accept(method);
        return this;
    }

    public MethodDeclaration createConstructor() {
        final var method = new MethodDeclaration()
                .kind(ElementKind.CONSTRUCTOR)
                .simpleName(getSimpleName())
                .returnType(new NoTypeExpression(TypeKind.VOID));
        addEnclosed(method);
        method.setEnclosing(this);
        return method;
    }

    public ClassDeclaration method(final Consumer<MethodDeclaration> methodBuilder) {
        final var method = new MethodDeclaration()
                .kind(ElementKind.METHOD);
        method.setEnclosing(this);
        methodBuilder.accept(method);
        addEnclosed(method);
        return this;
    }

    public MethodDeclaration createMethod() {
        final var method = new MethodDeclaration().kind(ElementKind.METHOD);
        addEnclosed(method);
        method.setEnclosing(this);
        return method;
    }

    public void addEnclosed(final Tree child) {
        this.enclosed.add(child);
    }

    private void addEnclosed(final int index,
                             final Tree child) {
        this.enclosed.add(index, child);
    }

    public void addEnclosed(final Collection<Tree> enclosed) {
        this.enclosed.addAll(enclosed);
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

    public ClassDeclaration annotation(final String className) {
        return annotation(new AnnotationExpression(className));
    }

    public ClassDeclaration annotation(final AnnotationExpression annotationExpression) {
        this.annotations.add(annotationExpression);
        return this;
    }

    public MethodDeclaration getPrimaryConstructor() {
        return primaryConstructor;
    }

    public void setPrimaryConstructor(final MethodDeclaration primaryConstructor) {
        this.primaryConstructor = primaryConstructor;
    }

    public Expression getExtending() {
        return extending;
    }

    public void setExtending(final Expression extending) {
        this.extending = extending;
    }

    public List<Expression> getImplementing() {
        return implementing;
    }

    public void addImplement(final Expression expression) {
        this.implementing.add(expression);
    }

    public ClassSymbol getClassSymbol() {
        return classSymbol;
    }

    @Override
    public <R, P> R accept(final TreeVisitor<R, P> visitor, final P param) {
        return visitor.visitClassDeclaration(this, param);
    }

    public ClassDeclaration classSymbol(final ClassSymbol classSymbol) {
        this.classSymbol = classSymbol;
        return this;
    }

}
