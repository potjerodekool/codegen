package io.github.potjerodekool.codegen.loader.java.visitor.signature;

import org.objectweb.asm.signature.SignatureVisitor;

public class LogSignatureVisitor extends SignatureVisitor {
    public LogSignatureVisitor(final int api) {
        super(api);
    }

    @Override
    public void visitFormalTypeParameter(final String name) {
        log("visitFormalTypeParameter " + name);
    }

    @Override
    public SignatureVisitor visitClassBound() {
        log("visitClassBound");
        return this;
    }

    @Override
    public SignatureVisitor visitInterfaceBound() {
        log("visitInterfaceBound");
        return this;
    }

    @Override
    public SignatureVisitor visitSuperclass() {
        log("visitSuperclass");
        return this;
    }

    @Override
    public SignatureVisitor visitInterface() {
        log("visitInterface");
        return this;
    }

    @Override
    public SignatureVisitor visitParameterType() {
        log("visitParameterType");
        return this;
    }

    @Override
    public SignatureVisitor visitReturnType() {
        log("visitReturnType");
        return this;
    }

    @Override
    public SignatureVisitor visitExceptionType() {
        log("visitExceptionType");
        return this;
    }

    @Override
    public void visitBaseType(final char descriptor) {
        log("visitBaseType " + descriptor);
    }

    @Override
    public void visitTypeVariable(final String name) {
        log("visitTypeVariable " + name);
    }

    @Override
    public SignatureVisitor visitArrayType() {
        log("visitArrayType");
        return this;
    }

    @Override
    public void visitClassType(final String name) {
        log("visitClassType " + name);
    }

    @Override
    public void visitInnerClassType(final String name) {
        log("visitInnerClassType " + name);
    }

    @Override
    public void visitTypeArgument() {
        log("visitTypeArgument");
    }

    @Override
    public SignatureVisitor visitTypeArgument(final char wildcard) {
        log("visitTypeArgument " + wildcard);
        return this;
    }

    @Override
    public void visitEnd() {
        log("visitEnd");
    }

    private void log(final String message) {
        System.out.println(message);
    }
}
