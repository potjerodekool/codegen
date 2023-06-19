package io.github.potjerodekool.codegen.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileManagerImpl implements FileManager {

    private final Map<Location, List<Path>> locationPaths = new HashMap<>();

    @Override
    public FileObject getResource(final Location location,
                                  final CharSequence moduleAndPkg,
                                  final String relativeName) {
        final var fileName = resolveName(moduleAndPkg, relativeName);
        final var paths = resolvePaths(location);

        if (paths.isEmpty()) {
            return null;
        }

        final var resolvedPathOptional = paths.stream()
                .map(path -> path.resolve(fileName))
                .filter(Files::exists)
                .findFirst();

        if (resolvedPathOptional.isEmpty()) {
            final var path = paths.get(0).resolve(fileName);
            return new PathFileObject(
                    path,
                    resolveKind(fileName)
            );
        }

        final var resolvedPath = resolvedPathOptional.get();
        return new PathFileObject(resolvedPath, resolveKind(fileName));
    }

    @Override
    public FileObject createResource(final Location location,
                                     final CharSequence moduleAndPkg,
                                     final String relativeName) {
        final var fileName = resolveName(moduleAndPkg, relativeName);
        final var paths = resolvePaths(location);

        if (paths.isEmpty()) {
            return null;
        }

        final var path = paths.get(0).resolve(fileName);
        final var parentPath = path.getParent();

        if (!Files.exists(parentPath)) {
            try {
                Files.createDirectories(parentPath);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }

        return new PathFileObject(path, resolveKind(fileName));
    }

    private String resolveName(final CharSequence moduleAndPkg,
                               final String relativeName) {
        final String fileName;

        if (moduleAndPkg != null && moduleAndPkg.length() > 0) {
            fileName = moduleAndPkg.toString().replace(".", "/") + "/" +  relativeName;
        } else {
            fileName = relativeName;
        }
        return fileName;
    }

    @Override
    public void setPathsForLocation(final Location location,
                                    final List<Path> paths) {
        this.locationPaths.put(location, paths);
    }

    private List<Path> resolvePaths(final Location location) {
        return this.locationPaths.getOrDefault(location, List.of());
    }

    private FileObject.Kind resolveKind(final String fileName) {
        if (fileName.endsWith(".properties")) {
            return FileObject.Kind.PROPERTIES;
        } else if (fileName.endsWith(".yml") || fileName.endsWith(".yaml")) {
            return FileObject.Kind.YAML;
        } else {
            return FileObject.Kind.UNKNOWN;
        }
    }
}
