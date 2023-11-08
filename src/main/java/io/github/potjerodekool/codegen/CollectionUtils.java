package io.github.potjerodekool.codegen;

import java.util.List;
import java.util.function.BiConsumer;

public final class CollectionUtils {

    private CollectionUtils() {
    }

    public static <T> void forEachWithIndexed(final List<T> list,
                                              final BiConsumer<T, Integer> consumer) {
        final var size = list.size();

        for (int i = 0; i < size; i++) {
            consumer.accept(list.get(i), i);
        }
    }
}
