package io.github.potjerodekool.codegen.template.adapter;

import org.stringtemplate.v4.Interpreter;
import org.stringtemplate.v4.ModelAdaptor;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.misc.STNoSuchPropertyException;

import java.util.Set;

public class SetModelAdapter implements ModelAdaptor<Set> {
    @Override
    public Object getProperty(final Interpreter interp,
                              final ST self,
                              final Set model,
                              final Object property,
                              final String propertyName) throws STNoSuchPropertyException {
        if ("isEmpty".equals(propertyName)) {
            return model.isEmpty();
        } else {
            throw new UnsupportedOperationException("Unsupported property: " + propertyName);
        }
    }
}
