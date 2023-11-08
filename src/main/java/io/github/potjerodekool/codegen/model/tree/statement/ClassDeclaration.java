package io.github.potjerodekool.codegen.model.tree.statement;

import io.github.potjerodekool.codegen.model.element.ElementKind;
import io.github.potjerodekool.codegen.model.element.Modifier;
import io.github.potjerodekool.codegen.model.element.Name;
import io.github.potjerodekool.codegen.model.symbol.ClassSymbol;
import io.github.potjerodekool.codegen.model.tree.*;
import io.github.potjerodekool.codegen.model.tree.expression.Expression;

import java.util.*;

public abstract class ClassDeclaration<CD extends ClassDeclaration<CD>> extends AbstractStatement implements WithMetaData {

    private final Name simpleName;

    private final List<Tree> enclosed = new ArrayList<>();
    private Tree enclosing;
    private final List<AnnotationExpression> annotations = new ArrayList<>();

    private MethodDeclaration<?> primaryConstructor;

    private Expression extending;

    private final List<Expression> implementing = new ArrayList<>();

    private final Map<String, Object> metaData = new HashMap<>();

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

    public Name getQualifiedName() {
        return qualifiedNameOf(this);
    }

    private Name qualifiedNameOf(final Tree tree) {
        if (tree instanceof ClassDeclaration<?> classDeclaration) {
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

    public abstract ElementKind getKind();

    public abstract Set<? extends Modifier> getModifiers();

    public List<Tree> getEnclosed() {
        return enclosed;
    }

    public void removeEnclosed(final Tree tree) {
        this.enclosed.remove(tree);
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

    public CD annotation(final String className) {
        return annotation(new AnnotationExpression(className));
    }

    public CD annotation(final AnnotationExpression annotationExpression) {
        this.annotations.add(annotationExpression);
        return (CD) this;
    }

    public MethodDeclaration<?> getPrimaryConstructor() {
        return primaryConstructor;
    }

    public void setPrimaryConstructor(final MethodDeclaration<?> primaryConstructor) {
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

    public abstract ClassSymbol getClassSymbol();

    public abstract List<? extends MethodDeclaration<?>> constructors();

    public abstract List<? extends MethodDeclaration<?>> methods();

    public abstract List<? extends VariableDeclaration<?>> fields();

}
