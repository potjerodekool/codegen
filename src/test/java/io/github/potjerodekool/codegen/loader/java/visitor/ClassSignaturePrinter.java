package io.github.potjerodekool.codegen.loader.java.visitor;

import io.github.potjerodekool.codegen.model.element.*;
import io.github.potjerodekool.codegen.model.type.*;
import io.github.potjerodekool.codegen.model.type.immutable.WildcardType;

public class ClassSignaturePrinter implements ElementVisitor<Object, Object>, TypeVisitor<Object, Object> {

    private final StringBuilder builder = new StringBuilder();

    @Override
    public Object visit(final Element e, final Object param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitPackage(final PackageElement e, final Object param) {
        return null;
    }

    @Override
    public Object visitType(final TypeElement e, final Object param) {
        final var type = e.asType();
        type.accept(this, null);

        if (e.getSuperclass() != null) {
            builder.append(" extends ");
            e.getSuperclass().accept(this, param);
        }

        final var interfaces = e.getInterfaces();

        if (interfaces.size() > 0) {
            builder.append(" implements ");
            final var lastIndex = interfaces.size() - 1;

            for (int i = 0; i < interfaces.size(); i++) {
                interfaces.get(i).accept(this, param);

                if (i < lastIndex) {
                    builder.append(", ");
                }
            }
        }

        return null;
    }

    @Override
    public Object visitVariable(final VariableElement e, final Object param) {
        return null;
    }

    @Override
    public Object visitExecutable(final ExecutableElement e, final Object param) {
        return null;
    }

    @Override
    public Object visitTypeParameter(final TypeParameterElement e, final Object param) {
        return null;
    }

    @Override
    public Object visitUnknown(final Element e, final Object param) {
        return null;
    }

    @Override
    public Object visit(final TypeMirror t, final Object param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitPrimitive(final PrimitiveType t, final Object param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitNull(final NullType t, final Object param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitArray(final ArrayType t, final Object param) {
        builder.append("[");
        t.getComponentType().accept(this, param);
        return null;
    }

    @Override
    public Object visitDeclared(final DeclaredType t, final Object param) {
        final var element = t.asElement();

        if (element instanceof QualifiedNameable qualifiedNameable) {
            builder.append(qualifiedNameable.getQualifiedName());
        } else {
            builder.append(element.getSimpleName());
        }

        final var typeArgs = t.getTypeArguments();

        if (typeArgs.size() > 0) {
            builder.append("<");
            final var lastIndex = typeArgs.size() - 1;

            for (int i = 0; i < typeArgs.size(); i++) {
                typeArgs.get(i).accept(this, param);
                if (i < lastIndex) {
                    builder.append(",");
                }
            }
            builder.append(">");
        }


        return null;
    }

    @Override
    public Object visitError(final ErrorType t, final Object param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitTypeVariable(final TypeVariable t, final Object param) {
        final var typeVariableElement = (TypeParameterElement) t.asElement();
        builder.append(typeVariableElement.getSimpleName());

        if (t.getUpperBound() != null) {
            builder.append(" extends ");
            t.getUpperBound().accept(this, param);
        } else if (t.getLowerBound() != null) {
            builder.append(" super ");
            t.getLowerBound().accept(this, param);
        }
        return null;
    }

    @Override
    public Object visitWildcard(final WildcardType t, final Object param) {
        if (t.getExtendsBound() != null) {
            throw new UnsupportedOperationException();
        } else if (t.getSuperBound() != null) {
            throw new UnsupportedOperationException();
        } else {
          builder.append("?");
        }
        return null;
    }

    @Override
    public Object visitExecutable(final ExecutableType t, final Object param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitNoType(final NoType t, final Object param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitUnknown(final TypeMirror t, final Object param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitUnion(final UnionType t, final Object param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitIntersection(final IntersectionType t, final Object param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitVarType(final VarType varType, final Object o) {
        throw new UnsupportedOperationException();
    }

    public String getSignature() {
        return builder.toString();
    }
}
