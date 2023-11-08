package io.github.potjerodekool.codegen.model.tree;

import io.github.potjerodekool.codegen.model.element.ElementKind;
import io.github.potjerodekool.codegen.model.tree.kotlin.KMethodDeclaration;
import io.github.potjerodekool.codegen.model.tree.statement.kotlin.KClassDeclaration;
import io.github.potjerodekool.codegen.model.tree.statement.kotlin.KVariableDeclaration;

import java.util.List;
import java.util.function.Predicate;

public final class KTreeFilter {

    private KTreeFilter() {
    }

    public static List<KMethodDeclaration> constructors(final KClassDeclaration classDeclaration) {
        return filterEnclosed(
                classDeclaration,
                methodDeclaration -> methodDeclaration.getKind() == ElementKind.CONSTRUCTOR,
                KMethodDeclaration.class);
    }

    public static List<KMethodDeclaration> methods(final KClassDeclaration classDeclaration) {
        return filterEnclosed(
                classDeclaration,
                methodDeclaration -> methodDeclaration.getKind() == ElementKind.METHOD,
                KMethodDeclaration.class);
    }

    public static List<KVariableDeclaration> fields(final KClassDeclaration classDeclaration) {
        return filterEnclosed(
                classDeclaration,
                variableDeclaration -> variableDeclaration.getKind() == ElementKind.FIELD,
                KVariableDeclaration.class);
    }

    private static <E extends Tree> List<E> filterEnclosed(final KClassDeclaration classDeclaration,
                                                           final Predicate<E> predicate,
                                                           final Class<E> clazz) {
        return classDeclaration.getEnclosed().stream()
                .filter(enclosed -> clazz.isAssignableFrom(enclosed.getClass()))
                .map(clazz::cast)
                .filter(predicate)
                .toList();
    }
}
