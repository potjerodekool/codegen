package io.github.potjerodekool.codegen.loader.asm.visitor.signature2;

import io.github.potjerodekool.codegen.loader.TypeElementLoader;
import io.github.potjerodekool.codegen.model.type.ClassType;
import io.github.potjerodekool.codegen.model.type.TypeMirror;
import io.github.potjerodekool.codegen.model.util.type.Types;
import org.objectweb.asm.signature.SignatureVisitor;

public abstract class AbstractSignatureVisitor2 extends SignatureVisitor {

    protected final TypeElementLoader loader;
    protected final Types types;

    protected final AbstractSignatureVisitor2 parent;

    public AbstractSignatureVisitor2(final int api,
                                     final TypeElementLoader loader,
                                     final Types types,
                                     final AbstractSignatureVisitor2 parent) {
        super(api);
        this.loader = loader;
        this.types = types;
        this.parent = parent;
    }

    @Override
    public void visitFormalTypeParameter(final String name) {
        throwUnsupported();
    }

    @Override
    public SignatureVisitor visitClassBound() {
        throwUnsupported();
        return null;
    }

    @Override
    public SignatureVisitor visitInterfaceBound() {
        throwUnsupported();
        return null;
    }

    @Override
    public SignatureVisitor visitSuperclass() {
        return new SuperClassOrInterfaceVisitor(api, loader, types, true, this);
    }

    @Override
    public SignatureVisitor visitInterface() {
        return new SuperClassOrInterfaceVisitor(api, loader, types, false, this);
    }

    @Override
    public SignatureVisitor visitParameterType() {
        throwUnsupported();
        return null;
    }

    @Override
    public SignatureVisitor visitReturnType() {
        throwUnsupported();
        return null;
    }

    @Override
    public SignatureVisitor visitExceptionType() {
        throwUnsupported();
        return null;
    }

    @Override
    public void visitBaseType(final char descriptor) {
        throwUnsupported();
    }

    @Override
    public void visitTypeVariable(final String name) {
        throwUnsupported();
    }

    @Override
    public SignatureVisitor visitArrayType() {
        return new ArrayTypeVisitor(api, loader,  types,this);
    }

    @Override
    public void visitClassType(final String name) {
        throwUnsupported();
    }

    @Override
    public void visitInnerClassType(final String name) {
        throwUnsupported();
    }

    @Override
    public void visitTypeArgument() {
        throwUnsupported();
    }

    @Override
    public SignatureVisitor visitTypeArgument(final char wildcard) {
        return new TypeArgumentVisitor(api, loader, types, this, wildcard);
    }

    @Override
    public void visitEnd() {
        throwUnsupported();
    }

    protected ClassType createClassType(final String name) {
        final var classSymbol = loader.loadTypeElement(name);
        return new ClassType(classSymbol, false);
    }

    protected void superClassEnd(final ClassType classType) {
        throwUnsupported();
    }

    protected void interfaceEnd(final ClassType classType) {
        throwUnsupported();
    }

    protected void throwUnsupported() {
        throw new UnsupportedOperationException(getClass().getName());
    }

    protected void addType(final TypeMirror type) {
        throwUnsupported();
    }

    protected void addTypeArgument(final TypeMirror type) {
        throwUnsupported();
    }
}
