package io.github.potjerodekool.codegen.model.type;

public enum TypeKind {
    BOOLEAN,

    BYTE,

    SHORT,

    INT,

    LONG,

    CHAR,

    FLOAT,

    DOUBLE,

    VOID,

    NONE,

    NULL,

    ARRAY,

    DECLARED,

    ERROR,

    TYPEVAR,

    WILDCARD,

    PACKAGE,

    EXECUTABLE,

    OTHER,

    UNION,

    INTERSECTION,

    MODULE,

    VAR;

    public boolean isPrimitive() {
        return switch (this) {
            case BOOLEAN, BYTE, SHORT, INT, LONG, CHAR, FLOAT, DOUBLE -> true;
            default -> false;
        };
    }

    public boolean isNoneType() {
        return this == NONE || this == VOID;
    }
}
