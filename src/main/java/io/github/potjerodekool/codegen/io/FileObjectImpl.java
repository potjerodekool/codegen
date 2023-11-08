package io.github.potjerodekool.codegen.io;

import java.io.*;

public class FileObjectImpl implements FileObject {
    @Override
    public String getName() {
        return null;
    }

    @Override
    public Kind getKind() {
        return null;
    }

    @Override
    public long getLastModified() {
        return 0;
    }

    @Override
    public InputStream openInputStream() {
        return null;
    }

    @Override
    public OutputStream openOutputStream() {
        return null;
    }

    @Override
    public Reader openReader(final boolean ignoreEncodingErrors) {
        return null;
    }

    @Override
    public CharSequence getCharContent(final boolean ignoreEncodingErrors) {
        return null;
    }

    @Override
    public Writer openWriter() throws IOException {
        return null;
    }
}
