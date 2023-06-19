package io.github.potjerodekool.codegen.model.element;

import io.github.potjerodekool.codegen.model.symbol.ClassSymbol;
import io.github.potjerodekool.codegen.model.symbol.MethodSymbol;
import io.github.potjerodekool.codegen.model.symbol.VariableSymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ElementFilter {

    private static final Set<ElementKind> RECORD_COMPONENT_KIND =
            Set.of(ElementKind.RECORD_COMPONENT);

    private ElementFilter() {}

    public static Stream<TypeElement> types(final ClassSymbol classSymbol) {
        return filterEnclosedElements(
                classSymbol,
                it -> switch (it.getKind()) {
                        case CLASS,
                             ANNOTATION_TYPE,
                             INTERFACE,
                             RECORD -> true;
                        default -> false;
                }
        ).map(it -> (TypeElement) it);
    }

    public static Stream<VariableSymbol> fields(final ClassSymbol typeElement) {
        return filterEnclosedElements(
                typeElement,
                it -> it.getKind() == ElementKind.FIELD
        ).map(it -> (VariableSymbol) it);
    }

    public static Stream<MethodSymbol> constructors(final ClassSymbol typeElement) {
        return constructors(typeElement, true);
    }

    public static Stream<MethodSymbol> constructors(final ClassSymbol typeElement,
                                                    final boolean includePrimaryConstructor) {
        final var constructorsStream = filterEnclosedElements(
                typeElement,
                it -> it.getKind() == ElementKind.CONSTRUCTOR
        ).map(it -> (MethodSymbol) it);

        final var primaryConstructor = includePrimaryConstructor ?
                typeElement.getPrimaryConstructor() : null;

        if (primaryConstructor == null) {
            return constructorsStream;
        } else {
            final var list = new ArrayList<MethodSymbol>();
            list.add(primaryConstructor);
            list.addAll(constructorsStream.toList());
            return list.stream();
        }
    }

    public static Stream<MethodSymbol> methods(final ClassSymbol typeElement) {
        return filterEnclosedElements(typeElement, it -> it.getKind() == ElementKind.METHOD)
                .map(it -> (MethodSymbol) it);
    }

    private static Stream<Element> filterEnclosedElements(final ClassSymbol element,
                                                          final Predicate<Element> filter) {
        return element.getEnclosedElements().stream()
                .filter(filter);
    }

    public static List<RecordComponentElement>
    recordComponentsIn(Iterable<? extends Element> elements) {
        return listFilter(elements, RECORD_COMPONENT_KIND, RecordComponentElement.class);
    }

    private static <E extends Element> List<E> listFilter(final Iterable<? extends Element> elements,
                                                          final Set<ElementKind> targetKinds,
                                                          final Class<E> clazz) {
        List<E> list = new ArrayList<>();
        for (Element e : elements) {
            if (targetKinds.contains(e.getKind()))
                list.add(clazz.cast(e));
        }
        return list;
    }
}
