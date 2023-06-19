package io.github.potjerodekool.codegen.model;

import io.github.potjerodekool.codegen.model.element.*;
import io.github.potjerodekool.codegen.model.symbol.ClassSymbol;
import io.github.potjerodekool.codegen.model.symbol.MethodSymbol;
import io.github.potjerodekool.codegen.model.symbol.VariableSymbol;
import io.github.potjerodekool.codegen.model.type.DeclaredType;
import io.github.potjerodekool.codegen.model.type.TypeMirror;
import io.github.potjerodekool.codegen.model.util.Elements;

import java.util.*;
import java.util.stream.Collectors;

public abstract class Attribute implements AnnotationValue {

    public static Attribute constant(final byte value) {
        return new Attribute.Constant(value);
    }

    public static Attribute constant(final boolean value) {
        return new Attribute.Constant(value);
    }

    public static Attribute constant(final char value) {
        return new Attribute.Constant(value);
    }

    public static Attribute constant(final short value) {
        return new Attribute.Constant(value);
    }

    public static Attribute constant(final int value) {
        return new Attribute.Constant(value);
    }

    public static Attribute constant(final long value) {
        return new Attribute.Constant(value);
    }

    public static Attribute constant(final float value) {
        return new Attribute.Constant(value);
    }

    public static Attribute constant(final double value) {
        return new Attribute.Constant(value);
    }

    public static Attribute constant(final String value) {
        return new Attribute.Constant(value);
    }

    public static Attribute array(final AnnotationValue value) {
        return array(List.of(value));
    }

    public static Attribute.Array array(final List<? extends AnnotationValue> values) {
        return new Attribute.Array(values);
    }

    public static Attribute clazz(final TypeMirror type) {
        return new Attribute.Class(type);
    }

    public static Attribute.Compound compound(final ClassSymbol classSymbol) {
        return new Attribute.Compound((DeclaredType) classSymbol.asType());
    }

    public static Attribute.Compound compound(final ClassSymbol classSymbol,
                                              final Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues) {
        return new Attribute.Compound((DeclaredType) classSymbol.asType(), elementValues);
    }

    public static Attribute.Compound compound(final ClassSymbol classSymbol,
                                              final Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues,
                                              final AnnotationTarget annotationTarget) {
        return new Attribute.Compound((DeclaredType) classSymbol.asType(), elementValues, annotationTarget);
    }

    public static Attribute.Compound compound(final ClassSymbol classSymbol,
                                              final Name attributeName,
                                              final AnnotationValue elementValue) {
        return new Attribute.Compound((DeclaredType) classSymbol.asType(), Map.of(
                MethodSymbol.createMethod(attributeName), elementValue
        ));
    }

    public static Attribute.Enum createEnumAttribute(final VariableElement variableElement) {
        return new Attribute.Enum(variableElement);
    }

    public static Attribute.Enum createEnumAttribute(final ClassSymbol classSymbol,
                                                     final Name variableName) {
        final var variableElement = VariableSymbol.createField(variableName.toString(), classSymbol.asType());
        variableElement.setEnclosingElement(classSymbol);
        return createEnumAttribute(variableElement);
    }

    public static class Constant extends Attribute {

        private final Object value;

        public Constant(final Object value) {
            this.value = value;
        }

        @Override
        public Object getValue() {
            return value;
        }

        @Override
        public <R, P> R accept(final AnnotationValueVisitor<R, P> v, final P p) {
            if (value instanceof Boolean b) {
                return v.visitBoolean(b, p);
            } else if (value instanceof Character c) {
                return v.visitChar(c, p);
            } else if (value instanceof Short s) {
                return v.visitShort(s, p);
            } else if (value instanceof Integer i) {
                return v.visitInt(i, p);
            } else if (value instanceof Long l) {
                return v.visitLong(l, p);
            } else if (value instanceof Float f) {
                return v.visitFloat(f, p);
            } else if (value instanceof Double d) {
                return v.visitDouble(d, p);
            } else if (value instanceof String s) {
                return v.visitString(s, p);
            } else {
                throw new UnsupportedOperationException("" + value);
            }
        }

        @Override
        public String toString() {
            if (value instanceof String) {
                return "\"" + value + "\"";
            } else {
                return value.toString();
            }
        }

    }

    public static class Array extends Attribute {

        private final List<AnnotationValue> values = new ArrayList<>();

        public Array(final List<? extends AnnotationValue> values) {
            this.values.addAll(values);
        }

        @Override
        public List<AnnotationValue> getValue() {
            return values;
        }

        @Override
        public <R, P> R accept(final AnnotationValueVisitor<R, P> visitor, final P param) {
            return visitor.visitArray(values, param);
        }

        @Override
        public String toString() {
            return values.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(", ", "{", "}"));
        }

    }

    public static class Class extends Attribute {

        private final TypeMirror classType;

        public Class(final TypeMirror classType) {
            this.classType = classType;
        }

        @Override
        public TypeMirror getValue() {
            return classType;
        }

        @Override
        public <R, P> R accept(final AnnotationValueVisitor<R, P> visitor, final P param) {
            return visitor.visitType(classType, param);
        }

        @Override
        public String toString() {
            return classType.toString();
        }

    }

    public static class Compound extends Attribute implements io.github.potjerodekool.codegen.model.element.AnnotationMirror {

        private final DeclaredType annotationType;
        private final Map<ExecutableElement, AnnotationValue> elementValues = new HashMap<>();
        private final AnnotationTarget target;

        public Compound(final DeclaredType annotationType) {
            this(annotationType, Map.of());
        }

        public Compound(final DeclaredType annotationType,
                        final Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues) {
            this(annotationType, elementValues, null);
        }

        public Compound(final DeclaredType annotationType,
                        final Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues,
                        final AnnotationTarget annotationTarget) {
            this.annotationType = annotationType;
            this.elementValues.putAll(elementValues);
            this.target = annotationTarget;
        }

        @Override
        public io.github.potjerodekool.codegen.model.type.DeclaredType getAnnotationType() {
            return annotationType;
        }

        @Override
        public Map<? extends ExecutableElement, ? extends AnnotationValue> getElementValues() {
            return elementValues;
        }

        @Override
        public Object getValue() {
            return this;
        }

        @Override
        public AnnotationTarget getTarget() {
            return target;
        }

        @Override
        public <R, P> R accept(final AnnotationValueVisitor<R, P> visitor, final P param) {
            return visitor.visitAnnotation(this, param);
        }

        public void addElementValue(final Name name, final AnnotationValue value) {
            this.elementValues.put(MethodSymbol.createMethod(name), value);
        }

        @Override
        public String toString() {
            final var stringBuilder = new StringBuilder();
            stringBuilder.append("@");
            stringBuilder.append(annotationType);

            if (elementValues.size() > 0) {
                stringBuilder.append("(");

                stringBuilder.append(
                    elementValues.entrySet().stream()
                            .map(entry -> entry.getKey().getSimpleName() + " = " + entry.getValue())
                            .collect(Collectors.joining(","))
                );

                stringBuilder.append(")");
            }

            return stringBuilder.toString();
        }
    }

    public static class Enum extends Attribute {

        private final VariableElement variableElement;

        public Enum(final VariableElement variableElement) {
            this.variableElement = variableElement;
        }

        @Override
        public VariableElement getValue() {
            return variableElement;
        }

        @Override
        public <R, P> R accept(final AnnotationValueVisitor<R, P> v, final P p) {
            return v.visitEnumConstant(variableElement, p);
        }

        @Override
        public String toString() {
            final var className = Elements.getQualifiedName(variableElement.getEnclosingElement());
            final var valueName = variableElement.getSimpleName();
            return className + "." + valueName;
        }
    }

}
