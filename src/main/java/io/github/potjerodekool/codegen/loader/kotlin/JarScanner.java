package io.github.potjerodekool.codegen.loader.kotlin;

import org.jetbrains.kotlin.builtins.KotlinBuiltIns;
import org.jetbrains.kotlin.descriptors.impl.ModuleDescriptorImpl;
import org.jetbrains.kotlin.name.FqName;
import org.jetbrains.kotlin.name.Name;
import org.jetbrains.kotlin.serialization.deserialization.DeserializedPackageFragment;
import org.jetbrains.kotlin.serialization.deserialization.builtins.BuiltInsPackageFragmentImpl;
import org.jetbrains.kotlin.storage.LockBasedStorageManager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

class JarScanner {

    private final KotlinBuiltIns buildIns;

    public JarScanner(final KotlinBuiltIns buildIns) {
        this.buildIns = buildIns;
    }

    List<DeserializedPackageFragment> scan(final URL url) {
        final List<DeserializedPackageFragment> builtInsPackageFragments = new ArrayList<>();
        final var storageManager = new LockBasedStorageManager("storage");

        final var moduleName = resolveModuleName(url);

        final var module = new ModuleDescriptorImpl(
                moduleName,
                storageManager,
                buildIns,
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