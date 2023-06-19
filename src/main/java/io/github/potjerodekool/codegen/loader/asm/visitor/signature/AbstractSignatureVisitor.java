package io.github.potjerodekool.codegen.loader.asm.visitor.signature;

import io.github.potjerodekool.codegen.loader.TypeElementLoader;
import io.github.potjerodekool.codegen.model.type.*;
import org.objectweb.asm.signature.SignatureVisitor;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;

public class AbstractSignatureVisitor extends SignatureVisitor {

    protected final TypeElementLoader loader;

    private final Deque<AbstractType> stack = new ArrayDeque<>();

    protected AbstractSignatureVisitor(final int api,
                                       final TypeElementLoader loader) {
        super(api);
        this.loader = loader;
    }

    @Override
    public void visitFormalTypeParameter(final String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SignatureVisitor visitClassBound() {
        return new ClassOrInterfaceBoundVisitor(api,  loader,this, true);
    }

    @Override
    public SignatureVisitor visitInterfaceBound() {
        return new ClassOrInterfaceBoundVisitor(api, loader,this, false);
    }

    @Override
    public SignatureVisitor visitSuperclass() {
        return new SuperClassOrInterfaceVisitor(api, loader, this, true);
    }

    @Override
    public SignatureVisitor visitInterface() {
        return new SuperClassOrInterfaceVisitor(api, loader, this, false);
    }

    @Override
    public SignatureVisitor visitParameterType() {
        throw new UnsupportedOperationException("" + getClass().getName());
    }

    @Override
    public SignatureVisitor visitReturnType() {
        throw new UnsupportedOperationException("" + getClass().getName());
    }

    @Override
    public SignatureVisitor visitExceptionType() {
        throw new UnsupportedOperationException("" + getClass().getName());
    }

    @Override
    public void visitBaseType(final char descriptor) {
        throw new UnsupportedOperationException("" + getClass().getName());
    }

    @Override
    public void visitTypeVariable(final String name) {
        throw new UnsupportedOperationException("" + getClass().getName());
    }

    @Override
    public SignatureVisitor visitArrayType() {
        return new ArrayTypeVisitor(
                api,
                loader,
                this
        );
    }

    @Override
    public void visitClassType(final String name) {
        throw new UnsupportedOperationException("" + getClass().getName());
    }

    protected ClassType createClassType(final String name) {
        final var classSymbol = loader.loadTypeElement(name);
        return new ClassType(classSymbol, false);
    }

    @Override
    public void visitInnerClassType(final String name) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    public void visitTypeArgument() {
        throw new UnsupportedOperationException("" + getClass().getName());
    }

    @Override
    public SignatureVisitor visitTypeArgument(final char wildcard) {
        return new TypeArgumentVisitor(api, loader, this, wildcard);
    }

    @Override
    public void visitEnd() {
        throw new UnsupportedOperationException("" + getClass().getName());
    }

    protected void classBoundEnd(final AbstractType type) {
        throw new UnsupportedOperationException("" + getClass().getName());
    }

    protected void interfacesBoundEnd(final AbstractType type) {
        throw new UnsupportedOperationException("" + getClass().getName());
    }

    protected void superClassEnd(final AbstractType type) {
        throw new UnsupportedOperationException("" + getClass().getName());
    }

    protected void interfaceEnd(final AbstractType type) {
        throw new UnsupportedOperationException("" + getClass().getName());
    }

    protected void typeVariable(final TypeVariable typeVariable) {
        throw new UnsupportedOperationException("" + getClass().getName());
    }

    protected void visitTypeArgument(final AbstractType type) {
        throw new UnsupportedOperationException("" + getClass().getName());
    }

    protected void visitTypeArgumentEnd() {
        throw new UnsupportedOperationException("" + getClass().getName());
    }

    public void push(final AbstractType type) {
        stack.push(type);
    }

    public AbstractType pop() {
        if (this.stack.size() == 0) {
            throw new NoSuchElementException();
        }

        return this.stack.pop();
    }

    public AbstractType peek() {
        final var type = this.stack.peek();

        if (type == null) {
            throw new NoSuchElementException();
        }

        return type;
    }

    public void addChild(final AbstractType type) {
        throw new UnsupportedOperationException();
    }

    protected void addTypeArgument(final TypeMirror type) {
        throw new UnsupportedOperationException();
    }
}
