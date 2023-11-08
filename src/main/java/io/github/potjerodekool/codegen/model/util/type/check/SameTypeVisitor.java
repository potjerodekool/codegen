package io.github.potjerodekool.codegen.model.util.type.check;

import static io.github.potjerodekool.codegen.model.util.Elements.getQualifiedName;

import io.github.potjerodekool.codegen.model.symbol.PackageSymbol;
import io.github.potjerodekool.codegen.model.type.*;
import io.github.potjerodekool.codegen.model.type.immutable.PackageType;
import io.github.potjerodekool.codegen.model.type.immutable.WildcardType;

import java.util.stream.IntStream;

public class SameTypeVisitor extends AbstractTypeRelation {

    public SameTypeVisitor() {
    }

    @Override
    public Boolean visitNoType(final NoType noType, final TypeMirror type) {
        if (noType.getKind() == TypeKind.VOID) {
            return type.getKind() == TypeKind.VOID;
        } else if (noType.getKind() == TypeKind.NONE) {
            return type.getKind() == TypeKind.NONE;
        } else if (noType.getKind() == TypeKind.PACKAGE) {
            if (type.getKind() == TypeKind.PACKAGE) {
                final var packageType = (PackageType) noType;
                final var otherPackageType = (PackageType) type;
                final PackageSymbol packageSymbol = packageType.asElement();
                final PackageSymbol otherPackageSymbol = otherPackageType.asElement();
                return packageSymbol.getQualifiedName().contentEquals(otherPackageSymbol.getQualifiedName());
            }
        }

        return false;
    }

    @Override
    public Boolean visitPrimitive(final PrimitiveType primitiveType,
                                  final TypeMirror p) {
        if (p instanceof PrimitiveType) {
            return primitiveType.getKind() == p.getKind();
        }
        return false;
    }

    @Override
    public Boolean visitArray(final ArrayType arrayType,
                              final TypeMirror type) {
        if (type instanceof ArrayType at) {
            return arrayType.getComponentType().accept(this, at.getComponentType());
        }
        return false;
    }

    @Override
    public Boolean visitDeclared(final DeclaredType declaredType,
                                 final TypeMirror type) {
        if (type instanceof DeclaredType dt) {
            final var element = declaredType.asElement();
            final var otherElement = dt.asElement();
            if (!getQualifiedName(element).contentEquals(getQualifiedName(otherElement))) {
                return false;
            } else {
                final var typeArguments = declaredType.getTypeArguments();
                final var otherTypeArguments = dt.getTypeArguments();

                if (otherTypeArguments.size() != typeArguments.size()) {
                    return false;
                }

                final var typeArgCount = typeArguments.size();
                final var visitor = this;

                return IntStream.range(0, typeArgCount)
                        .allMatch(typeArgIndex -> {
                            final TypeMirror typeArg = typeArguments.get(typeArgIndex);
                            final TypeMirror otherTypeArg = otherTypeArguments.get(typeArgIndex);
                            return typeArg.accept(visitor, otherTypeArg);
                        });
            }
        }
        return false;
    }

    @Override
    public Boolean visitError(final ErrorTypeImpl errorType, final TypeMirror type) {
        if (type instanceof ErrorTypeImpl e) {
            return errorType.asElement().getQualifiedName().contentEquals(e.asElement().getQualifiedName());
        }
        return false;
    }

    @Override
    public Boolean visitNull(final NullType nullType, final TypeMirror type) {
        return type instanceof NullType;
    }

    @Override
    public Boolean visitWildcard(final WildcardType wildcardType, final TypeMirror param) {
        if (param instanceof WildcardType wt) {
            final TypeMirror extendsBound = wildcardType.getExtendsBound();
            final TypeMirror otherExtendsBound = wt.getExtendsBound();
            final TypeMirror superBound = wildcardType.getSuperBound();
            final TypeMirror otherSuperBound = wt.getSuperBound();

            if (extendsBound != null) {
                return otherExtendsBound != null && extendsBound.accept(this, otherExtendsBound);
            } else if (superBound != null) {
                return otherSuperBound != null && superBound.accept(this, otherSuperBound);
            }
        }
        return false;
    }
}
