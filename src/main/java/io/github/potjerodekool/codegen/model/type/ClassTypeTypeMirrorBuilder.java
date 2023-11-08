package io.github.potjerodekool.codegen.model.type;

import io.github.potjerodekool.codegen.model.symbol.ClassSymbol;

public class ClassTypeTypeMirrorBuilder extends TypeMirrorBuilder<ClassType> {

    private ClassSymbol typeElement;

    private ClassTypeTypeMirrorBuilder enclosingTypeElementBuilder;

    public ClassTypeTypeMirrorBuilder(final ClassType classType) {
        super();
        throw new UnsupportedOperationException();
    }

    @Override
    public ClassType build() {
        return null;
    }

    public ClassSymbol getTypeElement() {
        return typeElement;
    }

    public void setTypeElement(final ClassSymbol typeElement) {
        this.typeElement = typeElement;
    }

    public ClassTypeTypeMirrorBuilder getEnclosingTypeElementBuilder() {
        return enclosingTypeElementBuilder;
    }

    public void setEnclosingTypeElementBuilder(final ClassTypeTypeMirrorBuilder enclosingTypeElementBuilder) {
        this.enclosingTypeElementBuilder = enclosingTypeElementBuilder;
    }
}
