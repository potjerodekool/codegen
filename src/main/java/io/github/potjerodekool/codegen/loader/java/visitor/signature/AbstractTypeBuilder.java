package io.github.potjerodekool.codegen.loader.java.visitor.signature;

import io.github.potjerodekool.codegen.model.symbol.ClassSymbol;
import io.github.potjerodekool.codegen.model.type.ClassType;
import io.github.potjerodekool.codegen.model.type.TypeMirror;
import io.github.potjerodekool.codegen.model.util.Elements;
import io.github.potjerodekool.codegen.model.util.type.Types;
import org.objectweb.asm.signature.SignatureVisitor;

import java.util.ArrayList;
import java.util.Objects;

public abstract class AbstractTypeBuilder extends SignatureVisitor {

    protected final Elements elements;
    protected final Types types;
    protected final AbstractTypeBuilder parent;
    private TypeMirror currentType;

    public AbstractTypeBuilder(final int api,
                               final Elements elements,
                               final Types types,
                               final AbstractTypeBuilder parent) {
        super(api);
        this.elements = Objects.requireNonNull(elements);
        this.types = Objects.requireNonNull(types);
        this.parent = parent;
    }

    public TypeMirror getCurrentType() {
        return currentType;
    }

    public void setCurrentType(final TypeMirror currentType) {
        this.currentType = currentType;
    }

    @Override
    public SignatureVisitor visitInterfaceBound() {
        System.out.println("visitInterfaceBound");
        return this;
    }

    @Override
    public SignatureVisitor visitSuperclass() {
        return new SuperTypeBuilder(api, elements, types, this);
    }

    @Override
    public SignatureVisitor visitInterface() {
        return new InterfaceTypeBuilder(api, elements, types, this);
    }

    @Override
    public SignatureVisitor visitParameterType() {
        System.out.println("visitParameterType");
        return this;
    }

    @Override
    public SignatureVisitor visitReturnType() {
        System.out.println("visitReturnType");
        return this;
    }

    @Override
    public SignatureVisitor visitExceptionType() {
        System.out.println("visitExceptionType");
        return this;
    }

    @Override
    public void visitBaseType(final char descriptor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visitTypeVariable(final String name) {
        throw new UnsupportedOperationException("name " + name + " " + getClass().getName());
    }

    @Override
    public SignatureVisitor visitArrayType() {
        return new ArrayTypeBuilder(api, elements, types, this);
    }

    @Override
    public void visitClassType(final String name) {
        System.out.println("visitClassType " + name);
        throw new UnsupportedOperationException();
    }

    @Override
    public void visitInnerClassType(final String name) {
        final var outerType = (ClassType) getCurrentType();
        final var outerName = outerType.asElement().getQualifiedName().toString();
        final var innerElement = (ClassSymbol) elements.getTypeElement(outerName + "$" + name);
        final var innerType = new ClassType(
                outerType,
                new ArrayList<>(),
                new ArrayList<>(),
                true,
                innerElement
        );
        setCurrentType(innerType);
    }

    @Override
    public void visitTypeArgument() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SignatureVisitor visitTypeArgument(final char wildcard) {
        return new TypeArgumentBuilder(
                api,
                elements,
                types,
                this,
                wildcard
        );
    }

    @Override
    public void visitEnd() {
        //System.out.println("visitEnd");
    }

    protected void childEnded() {
    }

    protected void addChildType(final TypeMirror childType) {
        throw new UnsupportedOperationException();
    }

    public ClassSymbol getClassSymbol() {
        return null;
    }

    protected ClassType loadClassType(final String name) {
        final var classElement = (ClassSymbol) elements.getTypeElement(name);
        return new ClassType(
                null,
                new ArrayList<>(),
                new ArrayList<>(),
                true,
                classElement
        );
    }
}
