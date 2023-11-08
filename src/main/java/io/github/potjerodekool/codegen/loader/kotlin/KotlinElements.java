package io.github.potjerodekool.codegen.loader.kotlin;

import io.github.potjerodekool.codegen.loader.java.JavaElements;
import io.github.potjerodekool.codegen.model.element.TypeElement;
import io.github.potjerodekool.codegen.model.util.AbstractElements;
import io.github.potjerodekool.codegen.model.util.SymbolTable;
import org.jetbrains.kotlin.builtins.jvm.JvmBuiltIns;
import org.jetbrains.kotlin.name.ClassId;
import org.jetbrains.kotlin.serialization.deserialization.ClassData;
import org.jetbrains.kotlin.serialization.deserialization.DeserializedPackageFragment;
import org.jetbrains.kotlin.storage.LockBasedStorageManager;

import java.net.URL;
import java.util.*;

public class KotlinElements extends AbstractElements {

    private final JavaElements parent;
    private final List<DeserializedPackageFragment> builtInsPackageFragments = new ArrayList<>();
    private final Map<ClassId, TypeElement> classes = new HashMap<>();

    public KotlinElements(final SymbolTable symbolTable,
                          final URL[] classPath,
                          final JavaElements javaElements) {
        super(symbolTable);
        this.parent = javaElements;

        final var storageManager = new LockBasedStorageManager("DefaultBuiltIns");
        final var builtIns = new JvmBuiltIns(storageManager, JvmBuiltIns.Kind.FROM_DEPENDENCIES);
        final var scanner = new JarScanner(builtIns);
        final List<DeserializedPackageFragment> builtInsPackageFragments = Arrays.stream(classPath)
                .filter(url -> url.getFile().endsWith(".jar"))
                .map(scanner::scan)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull).toList();
        this.builtInsPackageFragments.addAll(builtInsPackageFragments);
    }

    @Override
    protected TypeElement doLoadTypeElement(final CharSequence name) {
        final var internalName = toInternalName(name);
        final var classId = ClassId.fromString(internalName);

        var classSymbol = this.classes.get(classId);

        if (classSymbol != null) {
            return classSymbol;
        }

        if (parent != null) {
            classSymbol = parent.getTypeElement(name);

            if (classSymbol != null) {
                return classSymbol;
            }
        }

        ClassData classData = null;
        int index = 0;

        while (classData == null && index < builtInsPackageFragments.size()) {
            final var builtInsPackageFragment = builtInsPackageFragments.get(index);
            classData = builtInsPackageFragment.getClassDataFinder().findClassData(classId);
            index++;
        }

        if (classData != null) {
            final var builder = new KotlinClassSymbolBuilder(getSymbolTable(), classData.getNameResolver());
            classSymbol = builder.build(classData);
            classes.put(classId, classSymbol);
            return classSymbol;
        }

        return null;
    }

}


