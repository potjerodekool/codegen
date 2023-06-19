package io.github.potjerodekool.codegen.loader.asm;

import io.github.potjerodekool.codegen.loader.TypeElementLoader;
import io.github.potjerodekool.codegen.model.util.SymbolTable;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AsmTypeElementLoaderTest {

    private URL[] resolveClassPath() throws MalformedURLException {
        final String javaHome = System.getProperty("java.home");

        final var files = new File(javaHome + "/jmods").listFiles();

        if (files == null) {
            return new URL[0];
        } else {
            final var classPath = new ArrayList<URL>();

            for (File file : files) {
                if (file.getName().endsWith(".jmod")) {
                    classPath.add(file.toURI().toURL());
                }
            }

            classPath.add(new File("C:\\Users\\evert\\.m2\\repository\\io\\swagger\\core\\v3\\swagger-models\\2.2.6\\swagger-models-2.2.6.jar").toURI().toURL());

            return classPath.toArray(URL[]::new);
        }
    }

    @Test
    void test() throws IOException {
        final String javaHome = System.getProperty("java.home");
        final var javaBaseFile = new File(javaHome + "/jmods/java.base.jmod");
        final var symbolTable = new SymbolTable();

        final var classPath = resolveClassPath();
        final var loader = new AsmTypeElementLoader(classPath, symbolTable);

        try (final var zipFile = new ZipFile(javaBaseFile)) {
            zipFile.entries().asIterator().forEachRemaining(entry -> {
               if (entryFilter(entry)) {
                   var className = entry.getName().substring("classes/".length());
                   className = className.substring(0, className.length() - ".class".length());
                   className = className.replace('/', '.');

                   try {
                       final var clazz = getClass().getClassLoader().loadClass(className);
                       System.out.println(clazz.getName());
                       loadAndAssert(clazz, loader);
                   } catch (ClassNotFoundException e) {
                       //Ignore
                   }
               }
            });
        }
    }

    private boolean entryFilter(final ZipEntry entry) {
        return entry.getName().startsWith("classes/")
                && entry.getName().endsWith(".class")
                && !entry.getName().contains("$")
                && !entry.getName().endsWith("module-info.class")
                && !entry.getName().contains("com/");
    }


    @Test
    void loadTypeElement() throws MalformedURLException, ClassNotFoundException {
        final var classPath = resolveClassPath();
        final var symbolTable = new SymbolTable();
        final var loader = new AsmTypeElementLoader(classPath, symbolTable);

        //loadAndAssert(java.lang.constant.MethodTypeDesc.class, loader);
        /*
        loadAndAssert(Map.class, loader);
        loadAndAssert(ArrayList.class, loader);
        loadAndAssert(BigDecimal.class, loader);
        loadAndAssert(ChronoLocalDateTime.class, loader);

        C:\Users\evert\.m2\repository\io\swagger\core\v3\swagger-models\2.2.6\swagger-models-2.2.6.jar
        */
        //loadAndAssert("java.lang.invoke.ClassSpecializer", loader, "java.lang.invoke.ClassSpecializer<T extends java.lang.Object, K extends java.lang.Object, S extends java.lang.invoke.ClassSpecializer<T, K, S>$SpeciesData> extends java.lang.Object");

        loadAndAssert("java.util.Map.Entry", loader, "");

        //loadAndAssert("io.swagger.v3.oas.models.security.SecurityRequirement", loader);
    }

    private void loadAndAssert(final String className,
                               final TypeElementLoader loader,
                               final String expected) {
        final var element= loader.loadTypeElement(className);
        final var actual = element.toString();
        assertEquals(expected, actual);
    }

    private void loadAndAssert(final Class<?> clazz,
                               final TypeElementLoader loader) throws ClassNotFoundException {
        final var expected = asString(clazz);
        assertEquals(expected, loader.loadTypeElement(clazz.getName()).toString());
    }

    private String asString(final Class<?> clazz) {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(clazz.getTypeName());

        if (clazz.getTypeParameters().length > 0) {
            stringBuilder.append("<");

            stringBuilder.append(Arrays.stream(clazz.getTypeParameters())
                    .map(it -> {
                        final var sb = new StringBuilder();
                        sb.append(it.getName());
                        if (it.getBounds().length > 0) {
                            sb.append(" extends ");
                            sb.append(it.getBounds()[0].getTypeName());
                        }
                        return sb.toString();
                    })
                    .collect(Collectors.joining(", "))
            );
            stringBuilder.append(">");
        }

        if (clazz.getSuperclass() != null) {
            stringBuilder.append(" extends ").append(clazz.getGenericSuperclass().getTypeName());
        } else if (!clazz.getName().equals("java.lang.Object")) {
            stringBuilder.append(" extends java.lang.Object");
        }

        if (clazz.getInterfaces().length > 0) {
            if (clazz.isInterface()) {
                stringBuilder.append(" extends ");
            } else {
                stringBuilder.append(" implements ");
            }

            stringBuilder.append(Arrays.stream(clazz.getGenericInterfaces())
                    .map(Type::getTypeName)
                    .collect(Collectors.joining(", "))
            );
        }

        return stringBuilder.toString();
    }
}