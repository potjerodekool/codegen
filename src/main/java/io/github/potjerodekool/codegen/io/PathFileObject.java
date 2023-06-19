package io.github.potjerodekool.codegen.io;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.StringJoiner;

public class PathFileObject implements FileObject {

    private final Path path;
    private final Kind kind;

    public PathFileObject(final Path path,
                          final Kind kind) {
        this.path = path;
        this.kind = kind;
    }

    @Override
    public String getName() {
        final var separator = path.getFileSystem().getSeparator();
        final var nameJoiner = new StringJoiner(separator);
        final var nameCount = path.getNameCount();

        for (var nameIndex = 0; nameIndex < nameCount; nameIndex++) {
            final var subName = path.getName(nameIndex);
            nameJoiner.add(subName.toString());
        }

        return nameJoiner.toString();
    }

    @Override
    public Kind getKind() {
        return kind;
    }

    @Override
    public long getLastModified() {
        try {
            return Files.getLastModifiedTime(path).toMillis();
        } catch (final IOException e) {
            return 0;
        }
    }

    @Override
    public InputStream openInputStream() throws IOException {
        return Files.newInputStream(this.path);
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        return Files.newOutputStream(path);
    }

    @Override
    public Reader openReader(final boolean ignoreEncodingErrors) throws IOException {
        return Files.newBufferedReader(path);
    }

    @Override
    public CharSequence getCharContent(final boolean ignoreEncodingErrors) throws IOException {
        try (final InputStream in = openInputStream()) {
            return new String(in.readAllBytes());
        }
    }

    @Override
    public Writer openWriter() throws IOException {
        return Files.newBufferedWriter(path,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE);
    }
}
