package io.github.potjerodekool.codegen.model.util.type.check;

import io.github.potjerodekool.codegen.loader.TypeMapping;
import io.github.potjerodekool.codegen.model.symbol.ClassSymbol;
import io.github.potjerodekool.codegen.model.type.*;
import io.github.potjerodekool.codegen.model.type.immutable.WildcardType;
import io.github.potjerodekool.codegen.model.util.type.SimpleVisitor;
import io.github.potjerodekool.codegen.model.util.type.Types;

import static io.github.potjerodekool.codegen.model.element.Name.getQualifiedNameOf;

import java.util.stream.IntStream;

public class AssignableVisitor extends SimpleVisitor<Boolean, TypeMirror> {

    private Types types;

    public void init(final Types types) {
        this.types = types;
    }

    @Override
    public Boolean visitType(final TypeMirror type,
                             final TypeMirror type2) {
        return false;
    }

    @Override
    public Boolean visitNoType(final NoType noType,
                               final TypeMirror type) {
        return noType.getKind() == type.getKind();
    }

    @Override
    public Boolean visitPrimitive(final PrimitiveType primitiveType,
                                  final TypeMirror type) {
        if (primitiveType.getKind() == type.getKind()) {
            return true;
        }

        //TODO check with boxing types
        return false;
    }

    @Override
    public Boolean visitArray(final ArrayType arrayType,
                              final TypeMirror type) {
        return type instanceof ArrayType at
                && arrayType.getComponentType().accept(this, at.getComponentType());
    }

    @Override
    public Boolean visitDeclared(final DeclaredType declaredType,
                                 final TypeMirror type) {
        if (type instanceof DeclaredType dt) {
            if (getQualifiedNameOf(dt.asElement()).contentEquals(getQualifiedNameOf(declaredType.asElement()))) {
                return checkTypeArgs(declaredType, dt);
            } else {
                final var typeMapping = new TypeMapping(declaredType);

                final var classSymbol = (ClassSymbol) declaredType.asElement();
                final var superType = classSymbol.getSuperclass();
                final var interfaces = classSymbol.getInterfaces();

                if (superType != null) {
                    final var declaredSuperType = (DeclaredType) superType;
                    final var typeArgs = typeMapping.resolve(declaredSuperType);
                    final var superTypeElement = (ClassSymbol) declaredSuperType.asElement();

                    final var resolved = (ClassType) types.getDeclaredType(superTypeElement, typeArgs);
                    if (resolved.accept(this, type)) {
                        return true;
                    }
                }

                for (final TypeMirror anInterface : interfaces) {
                    final var declaredInterfaceType = (DeclaredType) anInterface;
                    final var typeArgs = typeMapping.resolve(declaredInterfaceType);
                    final var interfaceTypeElement = (ClassSymbol) declaredInterfaceType.asElement();
                    final var resolved = (ClassType) types.getDeclaredType(interfaceTypeElement, typeArgs);
                    if (resolved.accept(this, type)) {
                        return true;
                    }
                }

                return false;
            }
        }

        return false;
    }

    private Boolean checkTypeArgs(final DeclaredType declaredType,
                                  final DeclaredType type) {
        final var typeArguments = declaredType.getTypeArguments();
        final var otherTypeArguments = type.getTypeArguments();

        if (typeArguments.size() != otherTypeArguments.size()) {
            return false;
        }

        return IntStream.range(0, typeArguments.size())
                .allMatch(index -> {
                    final var typeArg = (TypeMirror) typeArguments.get(index);
                    final var otherTypeArg = (TypeMirror) otherTypeArguments.get(index);
                    return typeArg.accept(this, otherTypeArg);
                });
    }

    @Override
    public Boolean visitError(final ErrorTypeImpl errorType,
                              final TypeMirror type) {
        if (type instanceof ErrorTypeImpl et) {
            return errorType.asElement().getQualifiedName().contentEquals(et.asElement().getQualifiedName());
        }
        return false;
    }

    @Override
    public Boolean visitNull(final NullType nullType,
                             final TypeMirror type) {
        return type instanceof NullType;
    }

    @Override
    public Boolean visitWildcard(final WildcardType wildcardType,
                                 final TypeMirror type) {
        if (type instanceof WildcardType wt) {
            final var extendsBound = (TypeMirror) wildcardType.getExtendsBound();
            final var superBound = (TypeMirror) wildcardType.getSuperBound();

            if (extendsBound != null) {
                return wt.getExtendsBound() != null
                        && extendsBound.accept(this,  wt.getExtendsBound());
            } else if (superBound != null) {
                return wt.getSuperBound() != null
                        && superBound.accept(this, wt.getSuperBound());
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean visitExecutable(final ExecutableType executableType,
                                   final TypeMirror type) {
        return false;
    }

    @Override
    public Boolean visitTypeVariable(final TypeVariable typeVariable, final TypeMirror typeMirror) {
        return null;
    }

    @Override
    public Boolean visitVarType(final VarType varType, final TypeMirror typeMirror) {
        return false;
    }
}
