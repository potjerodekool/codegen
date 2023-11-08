package io.github.potjerodekool.codegen.model.type;

import io.github.potjerodekool.codegen.model.type.immutable.WildcardType;

public interface TypeVisitor<R, P> {

    R visit(TypeMirror t, P param);

    default R visit(TypeMirror t) {
        return visit(t, null);
    }

    R visitPrimitive(PrimitiveType t, P param);

    R visitNull(NullType t, P param);

    R visitArray(ArrayType t, P param);

    R visitDeclared(DeclaredType t, P param);

    R visitError(ErrorType t, P param);

    R visitTypeVariable(TypeVariable t, P param);

    R visitWildcard(WildcardType t, P param);

    R visitExecutable(ExecutableType t, P param);

    R visitNoType(NoType t, P param);

    R visitUnknown(TypeMirror t, P param);

    R visitUnion(UnionType t, P param);

    R visitIntersection(IntersectionType t, P param);

    R visitVarType(VarType varType, P p);
}
