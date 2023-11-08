package io.github.potjerodekool.codegen;

import io.github.potjerodekool.codegen.model.element.Modifier;

import java.util.Comparator;

public class ModifierSorter implements Comparator<Modifier> {

    public static final ModifierSorter INSTANCE = new ModifierSorter();

    @Override
    public int compare(final Modifier first,
                       final Modifier second) {
        if (first == second) {
            return 0;
        } else if (isVisibilityModifier(first)) {
            return -1;
        } else if (isVisibilityModifier(second)) {
            return 1;
        } else if (first == Modifier.STATIC) {
            return -1;
        } else if (second == Modifier.STATIC) {
            return 1;
        }
        return -1;
    }

    private boolean isVisibilityModifier(final Modifier modifier) {
        return switch (modifier) {
            case PUBLIC, PROTECTED, PRIVATE -> true;
            default -> false;
        };
    }
}
