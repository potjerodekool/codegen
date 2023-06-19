package io.github.potjerodekool.codegen.model.type;

import io.github.potjerodekool.codegen.model.element.AnnotationMirror;
import io.github.potjerodekool.codegen.model.symbol.ClassSymbol;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class ClassType extends AbstractType implements DeclaredType {

    private final ClassSymbol typeElement;
    private final boolean isNullable;
    private TypeMirror enclosingType;

    public ClassType(final ClassSymbol typeElement,
                     final boolean isNullable) {
        this.typeElement = typeElement;
        this.isNullable = isNullable;
    }

    public ClassType(final ClassSymbol typeElement,
                     final List<? extends AnnotationMirror> annotations,
                     final List<? extends TypeMirror> typeArguments,
                     final boolean isNullable) {
        super(annotations, typeArguments);
        this.typeElement = typeElement;
        this.isNullable = isNullable;
    }

    @Override
    public ClassSymbol asElement() {
        return typeElement;
    }

    @Override
    public <R,P> R accept(final TypeVisitor<R,P> visitor,
                          final P param) {
        return visitor.visitDeclared(this, param);
    }

    @Override
    public <R, P> R accept(final Visitor<R, P> visitor, final P p) {
        return visitor.visitDeclared(this, p);
    }

    @Override
    public TypeKind getKind() {
        return TypeKind.DECLARED;
    }

    @Override
    public boolean isDeclaredType() {
        return true;
    }

    @Override
    public TypeMirror getEnclosingType() {
        return enclosingType;
    }

    public void setEnclosingType(final TypeMirror enclosingType) {
        this.enclosingType = enclosingType;
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (obj instanceof DeclaredType otherType) {
            final var otherElement = (ClassSymbol) otherType.asElement();
            return this.typeElement.getQualifiedName().contentEquals(otherElement.getQualifiedName());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.asElement().getQualifiedName().hashCode();
    }

    @Override
    public boolean isNullable() {
        return isNullable;
    }

    @Override
    public DeclaredType asNullableType() {
        if (isNullable) {
            return this;
        } else {
            return new ClassType(
                    this.asElement(),
                    this.getAnnotationMirrors(),
                    getTypeArguments(),
                    true
            );
        }
    }

    @Override
    public ClassType asNonNullableType() {
        return !isNullable ? this : new ClassType(
                this.asElement(),
                getAnnotationMirrors(),
                getTypeArguments(),
                false
        );
    }

    @Override
    public String toString() {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(this.asElement().getQualifiedName().toString());

        if (getTypeArguments().size() > 0) {
            stringBuilder.append("<");

            stringBuilder.append(getTypeArguments().stream()
                            .map(Object::toString)
                                    .collect(Collectors.joining(", "))
            );
            stringBuilder.append(">");
        }

        return stringBuilder.toString();
    }
}
