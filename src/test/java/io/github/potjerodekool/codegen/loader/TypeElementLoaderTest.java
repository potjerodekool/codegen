package io.github.potjerodekool.codegen.loader;

import io.github.potjerodekool.codegen.Environment;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class TypeElementLoaderTest {

    @Test
    void loadTypeElement() throws MalformedURLException {
        final var url = new File("C:\\Users\\evert\\.m2\\repository\\io\\swagger\\core\\v3\\swagger-annotations\\2.2.6\\swagger-annotations-2.2.6.jar").toURI().toURL();
        final var javaClassPath = getJavaClassPath();
        final var classPath = new ArrayList<>(Arrays.asList(javaClassPath));
        classPath.add(url);

        var environment = new Environment(classPath.toArray(new URL[0]));
        final var element = environment.getElementUtils().getTypeElement("io.swagger.v3.oas.annotations.media.Schema.RequiredMode");
        assertNotNull(element);
    }

    public static URL[] getJavaClassPath() {
        String javaHome = System.getProperty("java.home");
        File[] files = (new File(javaHome + "/jmods")).listFiles();
        if (files == null) {
            return new URL[0];
        } else {
            ArrayList<URL> classPath = new ArrayList();
            File[] var3 = files;
            int var4 = files.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                File file = var3[var5];
                if (file.getName().endsWith(".jmod")) {
                    classPath.add((URL)action(() -> {
                        return file.toURI().toURL();
                    }));
                }
            }

            return (URL[])classPath.toArray((x$0) -> {
                return new URL[x$0];
            });
        }
    }

    private static <R> R action(Action<R> action) {
        try {
            return action.execute();
        } catch (RuntimeException var2) {
            throw var2;
        } catch (Exception var3) {
            throw new RuntimeException(var3);
        }
    }

    @Test
    void test() {
        final var fullName = "io.swagger.v3.oas.annotations.media.Schema.RequiredMode";

        var fromIndex = fullName.length() - 1;
        var index = fullName.lastIndexOf('.', fromIndex);
        if (index < 0) {
            return;
        }
        fromIndex = index;
        String parentName;

        do {
            index = fullName.lastIndexOf('.', fromIndex - 1);
            //System.out.println(fullName.substring(0, fromIndex));
            parentName = fullName.substring(0, fromIndex);
            fromIndex = index;
        } while (fromIndex > 0);

        fromIndex = 35;
        final var childString = fullName.substring(fromIndex + 1);

        System.out.println(childString);
    }


}

@FunctionalInterface
interface Action<R> {
    R execute() throws Exception;
}