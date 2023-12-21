package io.github.potjerodekool.codegen.template.adapter;

import io.github.potjerodekool.codegen.template.model.element.Elem;
import org.stringtemplate.v4.Interpreter;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.misc.STNoSuchPropertyException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ElementModelAdapter extends DelegatingModelAdapter<Elem> {

    private static final Member INVALID_MEMBER;

    static {
        Member invalidMember;
        try {
            invalidMember = ElementModelAdapter.class.getDeclaredField("INVALID_MEMBER");
        } catch (final NoSuchFieldException e) {
            invalidMember = null;
        }

        INVALID_MEMBER = invalidMember;
    }

    private final Map<String, Map<String, Member>> cache = new HashMap<>();


    @Override
    public Object getProperty(final Interpreter interp,
                              final ST self,
                              final Elem model,
                              final Object property,
                              final String propertyName) throws STNoSuchPropertyException {
        if (propertyName.startsWith("is")) {
            final var member = findMember(model, propertyName);

            if (member instanceof Method method) {
                try {
                    return method.invoke(model);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return super.getProperty(interp, self, model, property, propertyName);
    }

    private Member findMember(final Elem model,
                               final String propertyName) {
        final var clazz = model.getClass();
        final var methods = cache.computeIfAbsent(clazz.getName(), k -> new HashMap<>());

        return methods.computeIfAbsent(propertyName, k -> {
            try {
                return clazz.getMethod(propertyName);
            } catch (final NoSuchMethodException e) {
                return INVALID_MEMBER;
            }
        });
    }
}
