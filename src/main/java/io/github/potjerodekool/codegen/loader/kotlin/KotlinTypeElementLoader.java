package io.github.potjerodekool.codegen.loader.kotlin;

import io.github.potjerodekool.codegen.loader.AbstractTypeElementLoader;
import io.github.potjerodekool.codegen.loader.TypeElementLoader;
import io.github.potjerodekool.codegen.model.symbol.ClassSymbol;
import io.github.potjerodekool.codegen.model.util.SymbolTable;
import org.jetbrains.kotlin.builtins.KotlinBuiltIns;
import org.jetbrains.kotlin.descriptors.impl.ModuleDescriptorImpl;
import org.jetbrains.kotlin.name.ClassId;
import org.jetbrains.kotlin.name.FqName;
import org.jetbrains.kotlin.name.Name;
import org.jetbrains.kotlin.serialization.deserialization.ClassData;
import org.jetbrains.kotlin.serialization.deserialization.DeserializedPackageFragment;
import org.jetbrains.kotlin.serialization.deserialization.builtins.BuiltInsPackageFragmentImpl;
import org.jetbrains.kotlin.storage.LockBasedStorageManager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class KotlinTypeElementLoader extends AbstractTypeElementLoader {

    private final TypeElementLoader parent;
    private final List<DeserializedPackageFragment> builtInsPackageFragments = new ArrayList<>();

    public KotlinTypeElementLoader(final URL[] classPath,
                                   final TypeElementLoader parent,
                                   final SymbolTable symbolTable) {
        super(symbolTable);
        this.parent = parent;

        final var scanner = new JarScanner();
        final List<DeserializedPackageFragment> builtInsPackageFragments = Arrays.stream(classPath)
                .filter(url -> url.getFile().endsWith(".jar"))
                .map(scanner::scan)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull).toList();
        this.builtInsPackageFragments.addAll(builtInsPackageFragments);
    }

    @Override
    public ClassSymbol doLoadTypeElement(final String name) {
        if (parent != null) {
            final var classSymbol = parent.doLoadTypeElement(name);

            if (classSymbol != null) {
                return classSymbol;
            }
        }

        ClassData classData = null;
        int index = 0;

        final var classId = ClassId.fromString(name);

        while (classData == null && index < builtInsPackageFragments.size()) {
            final var builtInsPackageFragment = builtInsPackageFragments.get(index);
            classData = builtInsPackageFragment.getClassDataFinder().findClassData(classId);
            index++;
        }

        if (classData != null) {
            final var builder = new ClassSymbolBuilder(getSymbolTable(), classData.getNameResolver());
            return builder.build(classData);
        }
        return null;
    }

    private static class JarScanner {

        List<DeserializedPackageFragment> scan(final URL url) {
            final List<DeserializedPackageFragment> builtInsPackageFragments = new ArrayList<>();
            final var storageManager = new LockBasedStorageManager("storage");

            final var moduleName = resolveModuleName(url);

            final var module = new ModuleDescriptorImpl(
                    moduleName,
                    storageManager,
                    DefaultBuiltIns.INSTANCE,
                    null,
                    Map.of(),
                    moduleName
            );

            try (final var inputStream = new ZipInputStream(url.openStream())) {
                ZipEntry entry;
                ByteArrayInputStream data;
                FqName packageName;

                while ((entry = inputStream.getNextEntry()) != null) {
                    if (entry.getName().endsWith(".kotlin_builtins")) {
                        data = readEntry(inputStream, (int) entry.getSize());
                        packageName = resolvePackageName(entry.getName());

                        final var builtInsPackageFragment = BuiltInsPackageFragmentImpl.Companion.create(
                                packageName,
                                storageManager,
                                module,
                                data,
                                false);
                        builtInsPackageFragments.add(builtInsPackageFragment);

                    }
                }
            } catch (final IOException ignored) {
                //Ignore
            }

            return builtInsPackageFragments;
        }

        private Name resolveModuleName(final URL url) {
            final var file = url.getFile();
            final var start = file.lastIndexOf("/") + 1;
            final var end = file.lastIndexOf(".");
            return Name.special("<" + file.substring(start, end));
        }

        private ByteArrayInputStream readEntry(final ZipInputStream inputStream, final int size) throws IOException {
            final var data = new byte[size];
            int off = 0;
            int len = size;
            int read;

            while ((read = inputStream.read(data, off, len)) > 0) {
                off += read;
                len -= read;
            }

            return new ByteArrayInputStream(data);
        }
        private FqName resolvePackageName(final String fileName) {
            final var elements = fileName.split("/");
            final var packageElementCount = elements.length - 1;
            final var names = new ArrayList<String>(elements.length - 1);

            for (int i = 0; i < packageElementCount; i++) {
                names.add(elements[0]);
            }

            return FqName.fromSegments(names);
        }

    }

    private static class DefaultBuiltIns extends KotlinBuiltIns {

        public static final DefaultBuiltIns INSTANCE = new DefaultBuiltIns(true);

        protected DefaultBuiltIns(final boolean loadBuiltInsFromCurrentClassLoader) {
            super(new LockBasedStorageManager("DefaultBuiltIns"));
            if (loadBuiltInsFromCurrentClassLoader) {
                createBuiltInsModule(false);
            }
        }
    }

}

