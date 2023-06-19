package io.github.potjerodekool.codegen;

import io.github.potjerodekool.codegen.model.CompilationUnit;
import io.github.potjerodekool.codegen.model.tree.Tree;
import io.github.potjerodekool.codegen.model.type.TypeMirror;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CodeContext {

    private final @Nullable CodeContext parentContext;

    private final Object astNode;

    private final Map<String, TypeMirror> localVariables = new HashMap<>();

    public CodeContext(final Object astNode) {
        this(astNode, null);
    }

    private CodeContext(final Object astNode,
                        final @Nullable CodeContext parentContext) {
        this.astNode = astNode;
        this.parentContext = parentContext;
    }

    public CodeContext child() {
        return new CodeContext(astNode, this);
    }

    public CodeContext child(final Object astNode) {
        return new CodeContext(astNode, this);
    }

    public Object getAstNode() {
        return astNode;
    }

    public @Nullable CodeContext getParentContext() {
        return parentContext;
    }

    public void defineLocalVariable(final TypeMirror type,
                                    final String name) {
        localVariables.put(name, type);
    }

    public Optional<? extends TypeMirror> resolveLocalVariable(final String name) {
        final var resoledTypeOptional = Optional.ofNullable(localVariables.get(name));

        if (resoledTypeOptional.isPresent()) {
            return resoledTypeOptional;
        }

        if (parentContext == null) {
            return Optional.empty();
        }

        return parentContext.resolveLocalVariable(name);
    }

    public Optional<CompilationUnit> resolveCompilationUnit() {
        if (astNode instanceof CompilationUnit cu) {
            return Optional.of(cu);
        } else if (parentContext != null) {
            return parentContext.resolveCompilationUnit();
        } else {
            return Optional.empty();
        }
    }
}
