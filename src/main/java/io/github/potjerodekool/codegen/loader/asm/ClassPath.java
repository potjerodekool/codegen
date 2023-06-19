package io.github.potjerodekool.codegen.loader.asm;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

public final class ClassPath {

    private ClassPath() {
    }

    public static URL[] getJavaClassPath() {
        final String javaHome = System.getProperty("java.home");

        final var files = new File(javaHome + "/jmods").listFiles();

        if (files == null) {
            return new URL[0];
        } else {
            final var classPath = new ArrayList<URL>();

            for (File file : files) {
                if (file.getName().endsWith(".jmod")) {
                    classPath.add(action(() -> file.toURI().toURL()));
                }
            }

            return classPath.toArray(URL[]::new);
        }
    }

    private static <R> R action(final Action<R> action) {
        try {
            return action.execute();
        } catch (final RuntimeException e) {
            throw e;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}

@FunctionalInterface
interface Action<R> {

    R execute() throws Exception;
}