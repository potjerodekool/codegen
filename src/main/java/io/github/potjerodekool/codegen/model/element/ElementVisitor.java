package io.github.potjerodekool.codegen.model.element;

public interface ElementVisitor<R, P> {

    R visit(Element e, P param);

    default R visit(Element e) {
        return visit(e, null);
    }

    R visitPackage(PackageElement e, P param);

    R visitType(TypeElement e, P param);

    R visitVariable(VariableElement e, P param);

    R visitExecutable(ExecutableElement e, P param);

    R visitTypeParameter(TypeParameterElement e, P param);

    R visitUnknown(Element e, P param);

    default R visitModule(ModuleElement e, P param) {
        return visitUnknown(e, param);
    }

    default R visitRecordComponent(RecordComponentElement e, P param) {
        return visitUnknown(e, param);
    }
}
