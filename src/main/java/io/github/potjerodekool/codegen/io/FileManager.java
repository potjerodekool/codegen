package io.github.potjerodekool.codegen.io;

import java.nio.file.Path;
import java.util.List;

public interface FileManager {

    FileObject getResource(Location location, CharSequence moduleAndPkg, String relativeName);

    FileObject createResource(Location location, CharSequence moduleAndPkg, String relativeName);

    void setPathsForLocation(Location location,
                             List<Path> paths);
}
