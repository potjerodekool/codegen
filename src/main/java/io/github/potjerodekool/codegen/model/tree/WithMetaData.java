package io.github.potjerodekool.codegen.model.tree;

import java.util.Map;

public interface WithMetaData {

    Map<String, Object> getMetaData();

    default <R> R getMetaData(final String key) {
        return (R) getMetaData().get(key);
    }

    default void setMetaData(final String key,
                             final Object data) {
        getMetaData().put(key, data);
    }
}
