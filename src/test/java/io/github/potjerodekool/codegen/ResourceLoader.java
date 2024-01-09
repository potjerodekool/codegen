package io.github.potjerodekool.codegen;

import java.io.IOException;

public final class ResourceLoader {

    private ResourceLoader() {
    }

    public static String load(final String path) {
        try (final var inputStream = ResourceLoader.class.getClassLoader().getResourceAsStream(path)) {
            if (inputStream != null) {
                return new String(inputStream.readAllBytes());
            } else {
                throw new IOException(String.format("Resource not found: %s", path));
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
