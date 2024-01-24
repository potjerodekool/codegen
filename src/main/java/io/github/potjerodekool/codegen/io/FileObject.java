package io.github.potjerodekool.codegen.io;

import java.io.*;

public interface FileObject {

    String getName();

    Kind getKind();

    long getLastModified();

    InputStream openInputStream() throws IOException;

    OutputStream openOutputStream() throws IOException;

    default void writeToOutputStream(final byte[] data) {
        try(var outputStream = openOutputStream()) {
            if (outputStream instanceof BufferedOutputStream bufferedOutputStream) {
                bufferedOutputStream.write(data);
            } else {
                try (var bufferedOutputStream = new BufferedOutputStream(outputStream)) {
                    bufferedOutputStream.write(data);
                }
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }


    Reader openReader(boolean ignoreEncodingErrors) throws IOException;

    CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException;

    Writer openWriter() throws IOException;

    enum Kind {
        PROPERTIES,
        YAML,
        UNKNOWN
    }
}
