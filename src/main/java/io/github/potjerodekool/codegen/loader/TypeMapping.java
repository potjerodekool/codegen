package io.github.potjerodekool.codegen.loader;

import io.github.potjerodekool.codegen.model.symbol.ClassSymbol;
import io.github.potjerodekool.codegen.model.type.DeclaredType;
import io.github.potjerodekool.codegen.model.type.TypeMirror;
import io.github.potjerodekool.codegen.model.type.TypeVariable;

import java.util.HashMap;
import java.util.Map;

public class TypeMapping {

    private final Map<String, TypeMirror> mapping = new HashMap<>();

    public TypeMapping(final DeclaredType declaredType) {
        final var element = (ClassSymbol) declaredType.asElement();
        final var typeParameterNames = element.getTypeParameters().stream()
                .map(typeParameter -> typeParameter.getSimpleName().toString())
                .toList();

        final var typeArguments = declaredType.getTypeArguments();

        for (int typeParamIndex = 0; typeParamIndex < typeParameterNames.size(); typeParamIndex++) {
            final var typeVariableName = typeParameterNames.get(typeParamIndex);
            if (typeParamIndex >= typeArguments.size()) {
                throw new ArrayIndexOutOfBoundsException();
            }
            final var typeArg = typeArguments.get(typeParamIndex);
            mapping.put(typeVariableName, typeArg);
        }
    }

    public TypeMirror[] resolve(final DeclaredType declaredType) {
        final var typeArgs = declaredType.getTypeArguments();
        return typeArgs.stream()
                        .map(it -> {
                            if (it instanceof TypeVariable typeVariable) {
                                final var name = typeVariable.asElement().getSimpleName();
                                return mapping.getOrDefault(name.toString(), it);
                            }
                            return it;
                        })
                                .toArray(TypeMirror[]::new);

    }
}
