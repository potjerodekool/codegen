package io.github.potjerodekool.codegen;

import io.github.potjerodekool.codegen.model.element.Modifier;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ModifierSorterTest {

    @Test
    void compare() {
        assertEquals(
                List.of(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL),
                sort(Set.of(Modifier.FINAL, Modifier.PUBLIC, Modifier.STATIC))
        );
    }

    private List<Modifier> sort(final Set<Modifier> modifiers) {
        return modifiers.stream()
                .sorted(new ModifierSorter())
                .toList();
    }
}