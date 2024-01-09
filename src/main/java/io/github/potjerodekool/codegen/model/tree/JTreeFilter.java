package io.github.potjerodekool.codegen.model.tree;

import io.github.potjerodekool.codegen.model.element.ElementKind;
import io.github.potjerodekool.codegen.model.tree.statement.ClassDeclaration;
import io.github.potjerodekool.codegen.model.tree.statement.VariableDeclaration;

import java.util.List;
import java.util.function.Predicate;

public class JTreeFilter {

    private JTreeFilter() {
    }

    public static List<MethodDeclaration> constructors(final ClassDeclaration classDeclaration) {
        return filterEnclosed(
                classDeclaration,
                methodDeclaration -> methodDeclaration.getKind() == ElementKind.CONSTRUCTOR,
                MethodDeclaration.class);
    }

    public static List<MethodDeclaration> methods(final ClassDeclaration classDeclaration) {
        return filterEnclosed(
                classDeclaration,
                methodDeclaration -> methodDeclaration.getKind() == ElementKind.METHOD,
                MethodDeclaration.class);
    }

    public static List<VariableDeclaration> fields(final ClassDeclaration classDeclaration) {
        return filterEnclosed(
                classDeclaration,
                variableDeclaration -> variableDeclaration.getKind() == ElementKind.FIELD,
                VariableDeclaration.class);
    }

    private static <E extends Tree> List<E> filterEnclosed(final ClassDeclaration classDeclaration,
                                                           final Predicate<E> predicate,
                                                           final Class<E> clazz) {
        return classDeclaration.getEnclosed().stream()
                .filter(enclosed -> clazz.isAssignableFrom(enclosed.getClass()))
                .map(clazz::cast)
                .filter(predicate)
                .toList();
    }
}
