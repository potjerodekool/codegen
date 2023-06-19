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

public class ClassDeclaration extends AbstractStatement {

    private final Name simpleName;
    private final ElementKind kind;

    private final Set<Modifier> modifiers = new HashSet<>();
    private final List<Tree> enclosed = new ArrayList<>();
    private Tree enclosing;
    private final List<AnnotationExpression> annotations = new ArrayList<>();

    private MethodDeclaration primaryConstructor;

    private Expression extending;

    private final List<Expression> implementing = new ArrayList<>();

    private ClassSymbol classSymbol;

    public ClassDeclaration(final Name simpleName,
                            final ElementKind kind,
                            final Set<Modifier> modifiers,
                            final List<Tree> definitions) {
        this.simpleName = simpleName;
        this.kind = kind;
        addModifiers(modifiers);
        addEnclosed(definitions);
    }

    public Name getSimpleName() {
        return simpleName;
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

    public MethodDeclaration addMethod(final String name,
                                       final Set<Modifier> modifiers) {
        return addMethod(new NoTypeExpression(TypeKind.VOID), name, modifiers);
    }

    public MethodDeclaration addMethod(final Expression returnType,
                                       final String name,
                                       final Set<Modifier> modifiers) {
        final var method = new MethodDeclaration(
                Name.of(name),
                ElementKind.METHOD,
                returnType,
                List.of(),
                List.of(),
                null
        );
        method.addModifiers(modifiers);
        addEnclosed(method);
        method.setEnclosing(this);
        return method;
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

    public MethodDeclaration getPrimaryConstructor() {
        return primaryConstructor;
    }

    public void setPrimaryConstructor(final MethodDeclaration primaryConstructor) {
        this.primaryConstructor = primaryConstructor;
    }

    public Expression getExtending() {
        return extending;
    }

    public List<Expression> getImplementing() {
        return implementing;
    }

    public MethodDeclaration addConstructor(final Set<Modifier> modifiers) {
        final var constructor = new MethodDeclaration(
                getSimpleName(),
                ElementKind.CONSTRUCTOR,
                new NoTypeExpression(TypeKind.VOID),
                List.of(),
                List.of(),
                null
        );
        constructor.addModifiers(modifiers);
        addEnclosed(constructor);
        constructor.setEnclosing(this);
        return constructor;
    }

    @Override
    public <R, P> R accept(final TreeVisitor<R, P> visitor, final P param) {
        return visitor.visitClassDeclaration(this, param);
    }

    public ClassSymbol getClassSymbol() {
        return classSymbol;
    }

    public void setClassSymbol(final ClassSymbol classSymbol) {
        this.classSymbol = classSymbol;
    }
}
