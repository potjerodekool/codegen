package io.github.potjerodekool.codegen.model.element;

import java.util.Objects;

public enum Modifier {

    PUBLIC,
    PROTECTED,
    PRIVATE,
    ABSTRACT,
    DEFAULT,
    STATIC,
    SEALED,
    NON_SEALED {
        public String toString() {
            return "non-sealed";
        }
    },
    FINAL,
    TRANSIENT,
    VOLATILE,
    SYNCHRONIZED,
    NATIVE,
    STRICTFP,
    VAR,

    //Kotlin
    VAL,
    OVERRIDE;

    public static Modifier toKotlinModifier(final Modifier modifier) {
        Objects.requireNonNull(modifier);

        if (modifier == Modifier.FINAL) {
            return Modifier.VAL;
        } else {
            return modifier;
        }
    }

    @Override
    public String toString() {
        return name().toLowerCase(java.util.Locale.US);
    }


}
