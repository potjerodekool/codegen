package io.github.potjerodekool.codegen.model.util;

import io.github.potjerodekool.codegen.model.AnnotatedConstruct;
import io.github.potjerodekool.codegen.model.element.*;

import java.util.*;

public interface Elements {

    PackageElement getPackageElement(CharSequence name);

    default PackageElement getPackageElement(ModuleElement module, CharSequence name) {
        return null;
    }

    default Set<? extends PackageElement> getAllPackageElements(CharSequence name) {
        Set<? extends ModuleElement> modules = getAllModuleElements();
        if (modules.isEmpty()) {
            PackageElement packageElt = getPackageElement(name);
            return (packageElt != null) ?
                    Collections.singleton(packageElt):
                    Collections.emptySet();
        } else {
            Set<PackageElement> result = new LinkedHashSet<>(1); // Usually expect at most 1 result
            for (ModuleElement module: modules) {
                PackageElement packageElt = getPackageElement(module, name);
                if (packageElt != null)
                    result.add(packageElt);
            }
            return Collections.unmodifiableSet(result);
        }
    }

    TypeElement getTypeElement(CharSequence name);

    default TypeElement getTypeElement(ModuleElement module, CharSequence name) {
        return null;
    }

    default Set<? extends TypeElement> getAllTypeElements(CharSequence name) {
        Set<? extends ModuleElement> modules = getAllModuleElements();
        if (modules.isEmpty()) {
            TypeElement typeElt = getTypeElement(name);
            return (typeElt != null) ?
                    Collections.singleton(typeElt):
                    Collections.emptySet();
        } else {
            Set<TypeElement> result = new LinkedHashSet<>(1); // Usually expect at most 1 result
            for (ModuleElement module: modules) {
                TypeElement typeElt = getTypeElement(module, name);
                if (typeElt != null)
                    result.add(typeElt);
            }
            return Collections.unmodifiableSet(result);
        }
    }

    default ModuleElement getModuleElement(CharSequence name) {
        return null;
    }

    default Set<? extends ModuleElement> getAllModuleElements() {
        return Collections.emptySet();
    }

    Map<? extends ExecutableElement, ? extends AnnotationValue>
    getElementValuesWithDefaults(AnnotationMirror a);

    String getDocComment(Element e);

    boolean isDeprecated(Element e);

    default javax.lang.model.util.Elements.Origin getOrigin(Element e) {
        return javax.lang.model.util.Elements.Origin.EXPLICIT;
    }

    default javax.lang.model.util.Elements.Origin getOrigin(AnnotatedConstruct c,
                                                            AnnotationMirror a) {
        return javax.lang.model.util.Elements.Origin.EXPLICIT;
    }

    default javax.lang.model.util.Elements.Origin getOrigin(ModuleElement m,
                                                            ModuleElement.Directive directive) {
        return javax.lang.model.util.Elements.Origin.EXPLICIT;
    }

    enum Origin {
        EXPLICIT,
        MANDATED,
        SYNTHETIC;
        public boolean isDeclared() {
            return this != SYNTHETIC;
        }
    }

    default boolean isBridge(ExecutableElement e) {
        return false;
    }

    Name getBinaryName(TypeElement type);

    static Name getQualifiedName(final Element element) {
        if (element instanceof QualifiedNameable qn) {
            return qn.getQualifiedName();
        } else {
            return element.getSimpleName();
        }
    }

    PackageElement getPackageOf(Element e);

    default ModuleElement getModuleOf(Element e) {
        return null;
    }

    List<? extends Element> getAllMembers(TypeElement type);

    List<? extends AnnotationMirror> getAllAnnotationMirrors(Element e);

    boolean hides(Element hider, Element hidden);

    boolean overrides(ExecutableElement overrider, ExecutableElement overridden,
                      TypeElement type);

    String getConstantExpression(Object value);

    void printElements(java.io.Writer w, Element... elements);

    Name getName(CharSequence cs);

    boolean isFunctionalInterface(TypeElement type);

    default boolean isAutomaticModule(ModuleElement module) {
        return false;
    }

    default RecordComponentElement recordComponentFor(ExecutableElement accessor) {
        if (accessor.getEnclosingElement().getKind() == ElementKind.RECORD) {
            for (RecordComponentElement rec : ElementFilter.recordComponentsIn(accessor.getEnclosingElement().getEnclosedElements())) {
                if (Objects.equals(rec.getAccessor(), accessor)) {
                    return rec;
                }
            }
        }
        return null;
    }
}
