package io.github.potjerodekool.codegen.model.tree;

import io.github.potjerodekool.codegen.model.element.ElementKind;
import io.github.potjerodekool.codegen.model.tree.java.JMethodDeclaration;
import io.github.potjerodekool.codegen.model.tree.statement.java.JClassDeclaration;
import io.github.potjerodekool.codegen.model.tree.statement.java.JVariableDeclaration;

import java.util.List;
import java.util.function.Predicate;

public class JTreeFilter {

    private JTreeFilter() {
    }

    public static List<JMethodDeclaration> constructors(final JClassDeclaration classDeclaration) {
        return filterEnclosed(
                classDeclaration,
                methodDeclaration -> methodDeclaration.getKind() == ElementKind.CONSTRUCTOR,
                JMethodDeclaration.class);
    }

    public static List<JMethodDeclaration> methods(final JClassDeclaration classDeclaration) {
        return filterEnclosed(
                classDeclaration,
                methodDeclaration -> methodDeclaration.getKind() == ElementKind.METHOD,
                JMethodDeclaration.class);
    }

    public static List<JVariableDeclaration> fields(final JClassDeclaration classDeclaration) {
        return filterEnclosed(
                classDeclaration,
                variableDeclaration -> variableDeclaration.getKind() == ElementKind.FIELD,
                JVariableDeclaration.class);
    }

    private static <E extends Tree> List<E> filterEnclosed(final JClassDeclaration classDeclaration,
                                                           final Predicate<E> predicate,
                                                           final Class<E> clazz) {
        return classDeclaration.getEnclosed().stream()
                .filter(enclosed -> clazz.isAssignableFrom(enclosed.getClass()))
                .map(clazz::cast)
                .filter(predicate)
                .toList();
    }
}
