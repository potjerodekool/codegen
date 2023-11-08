package io.github.potjerodekool.codegen.model.util.type;

import io.github.potjerodekool.codegen.model.element.*;
import io.github.potjerodekool.codegen.model.symbol.ClassSymbol;
import io.github.potjerodekool.codegen.model.type.*;
import io.github.potjerodekool.codegen.model.type.immutable.*;
import io.github.potjerodekool.codegen.model.type.java.immutable.JavaArrayTypeImpl;
import io.github.potjerodekool.codegen.model.type.java.immutable.JavaNoneType;
import io.github.potjerodekool.codegen.model.type.java.immutable.JavaVoidType;
import io.github.potjerodekool.codegen.model.util.Elements;
import io.github.potjerodekool.codegen.model.util.SymbolTable;
import io.github.potjerodekool.codegen.model.util.type.check.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaTypes implements Types {

    private static final SameTypeVisitor SAME_TYPE_VISITOR = new SameTypeVisitor();

    private static final SubTypeVisitor SUB_TYPE_VISITOR = new SubTypeVisitor();

    private static final AssignableVisitor ASSIGNABLE_VISITOR = new AssignableVisitor();

    private static final ContainsVisitor CONTAINS_VISITOR = new ContainsVisitor();

    private static final SubsignatureVisitor SUBSIGNATURE_VISITOR = new SubsignatureVisitor();

    private static final TypeErasureVisitor TYPE_ERASURE_VISITOR = new TypeErasureVisitor();

    private final Map<TypeKind, io.github.potjerodekool.codegen.model.type.PrimitiveType> primitiveTypeMap = Stream.of(
            PrimitiveTypeImpl.BOOLEAN,
            PrimitiveTypeImpl.BYTE,
            PrimitiveTypeImpl.SHORT,
            PrimitiveTypeImpl.INT,
            PrimitiveTypeImpl.LONG,
            PrimitiveTypeImpl.CHAR,
            PrimitiveTypeImpl.FLOAT,
            PrimitiveTypeImpl.DOUBLE
        ).collect(Collectors.toUnmodifiableMap(
            PrimitiveTypeImpl::getKind,
                  Function.identity()
        ));

    private final Map<TypeKind, String> boxMapping = new HashMap<>();
    private final Map<String, TypeKind> unBoxMapping = new HashMap<>();

    private final SymbolTable symbolTable;

    public JavaTypes(final SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
        initBoxingMappings();
        initVisitors();
    }

    private void initBoxingMappings() {
        initBoxingMapping(TypeKind.BOOLEAN, "java.lang.Boolean");
        initBoxingMapping(TypeKind.BYTE, "java.lang.Byte");
        initBoxingMapping(TypeKind.SHORT, "java.lang.Short");
        initBoxingMapping(TypeKind.INT, "java.lang.Integer");
        initBoxingMapping(TypeKind.LONG, "java.lang.Long");
        initBoxingMapping(TypeKind.CHAR, "java.lang.Character");
        initBoxingMapping(TypeKind.FLOAT, "java.lang.Float");
        initBoxingMapping(TypeKind.DOUBLE, "java.lang.Double");
        initBoxingMapping(TypeKind.VOID, "java.lang.Void");
    }

    private void initVisitors() {
        ASSIGNABLE_VISITOR.init(this);
    }

    private void initBoxingMapping(final TypeKind typeKind,
                                   final String boxClassName) {
        this.boxMapping.put(typeKind, boxClassName);
        this.unBoxMapping.put(boxClassName, typeKind);
    }

    @Override
    public Element asElement(final TypeMirror t) {
        if (t instanceof DeclaredType declaredType) {
            return declaredType.asElement();
        } else if (t instanceof TypeVariable typeVariable) {
            return typeVariable.asElement();
        } else {
            return null;
        }
    }

    @Override
    public boolean isSameType(final TypeMirror t1,
                              final TypeMirror t2) {
        if (t1.getKind() == TypeKind.WILDCARD
            || t2.getKind() == TypeKind.WILDCARD) {
            return false;
        }
        return SAME_TYPE_VISITOR.visit(t1, t2);
    }

    @Override
    public boolean isSubtype(final TypeMirror t1,
                             final TypeMirror t2) {
        throwIfExecutablePackageOrModule(t1);
        throwIfExecutablePackageOrModule(t2);

        return SUB_TYPE_VISITOR.visit(t1, t2);
    }

    private void throwIfExecutablePackageOrModule(final TypeMirror typeMirror) {
        if (typeMirror instanceof ExecutableType
                || typeMirror instanceof PackageType
                || typeMirror instanceof ModuleType) {
            throw new IllegalArgumentException("illegal type");
        }
    }

    private void throwIfPackageOrModule(final TypeMirror typeMirror) {
        if (typeMirror instanceof PackageType
                || typeMirror instanceof ModuleType) {
            throw new IllegalArgumentException("illegal type");
        }
    }

    @Override
    public boolean isAssignable(final TypeMirror t1,
                                final TypeMirror t2) {
        throwIfExecutablePackageOrModule(t1);
        throwIfExecutablePackageOrModule(t2);
        return ASSIGNABLE_VISITOR.visit(t1, t2);
    }

    @Override
    public boolean contains(final TypeMirror t1,
                            final TypeMirror t2) {
        throwIfExecutablePackageOrModule(t1);
        throwIfExecutablePackageOrModule(t2);
        return CONTAINS_VISITOR.visit(t1, t2);
    }

    @Override
    public boolean isSubsignature(final ExecutableType m1,
                                  final ExecutableType m2) {
        return SUBSIGNATURE_VISITOR.visitType(m1, m2);
    }

    @Override
    public List<? extends TypeMirror> directSupertypes(final TypeMirror t) {
        throwIfExecutablePackageOrModule(t);

        if (t instanceof DeclaredType declaredType) {
            final var directSuperTypes = new ArrayList<TypeMirror>();
            final var element = (TypeElement) declaredType.asElement();

            //TODO process type arguments

            final var superclass = element.getSuperclass();
            final var interfaces = element.getInterfaces();

            if (superclass != null) {
                directSuperTypes.add(superclass);
            }

            directSuperTypes.addAll(interfaces);

            /*
            if (element.getKind() == JavaElementKind.INTERFACE && directSuperTypes.isEmpty()) {
                directSuperTypes.add(getObjectType());
            }
            */

            return directSuperTypes;
        }
        return List.of();
    }

    /*
    private TypeMirror getObjectType() {
        final var element = typeElementLoader.loadTypeElement("java.lang.Object");
        return element.asType();
    }
    */

    @Override
    public TypeMirror erasure(final TypeMirror t) {
        throwIfPackageOrModule(t);
        return TYPE_ERASURE_VISITOR.visit(t, null);
    }

    @Override
    public TypeElement boxedClass(final PrimitiveType p) {
        final var typeKind = p.getKind();
        final var className = boxMapping.get(typeKind);
        return symbolTable.getClass(null, Name.of(className));
    }

    @Override
    public PrimitiveType unboxedType(final TypeMirror t) {
        if (t.getKind() != TypeKind.DECLARED) {
            throw new IllegalArgumentException("Not a declared type");
        }
        final var unboxedType = unBox((DeclaredType) t);

        if (unboxedType instanceof PrimitiveType primitiveType) {
            return primitiveType;
        } else {
            throw new IllegalArgumentException("Not a boxed type");
        }
    }

    private TypeMirror unBox(final DeclaredType declaredType) {
        final var className = Elements.getQualifiedName(declaredType.asElement()).toString();
        final var typeKind = unBoxMapping.get(className);

        if (typeKind.isPrimitive()) {
            return getPrimitiveType(typeKind);
        } else {
            return getNoType(typeKind);
        }
    }

    @Override
    public TypeMirror capture(final TypeMirror t) {
        throwIfExecutablePackageOrModule(t);
        throw new UnsupportedOperationException();
    }

    @Override
    public PrimitiveType getPrimitiveType(final TypeKind kind) {
        if (!kind.isPrimitive()) {
            throw new IllegalArgumentException(String.format("%s is not a primitive kind", kind));
        }
        return primitiveTypeMap.get(kind);
    }

    @Override
    public NullType getNullType() {
        return NullTypeImpl.INSTANCE;
    }

    @Override
    public NoType getNoType(final TypeKind kind) {
        if (kind == TypeKind.VOID) {
            return JavaVoidType.INSTANCE;
        } else if (kind == TypeKind.NONE) {
            return JavaNoneType.INSTANCE;
        } else {
            throw new IllegalArgumentException(String.format("%s is not a void or none type", kind));
        }
    }

    @Override
    public io.github.potjerodekool.codegen.model.type.ArrayType getArrayType(final TypeMirror componentType) {
        return new JavaArrayTypeImpl(componentType, false);
    }

    @Override
    public WildcardType getWildcardType(final TypeMirror extendsBound,
                                        final TypeMirror superBound) {
        if (extendsBound != null && superBound != null) {
            throw new IllegalArgumentException("Wildcard can not have both an extends bound and superBound");
        }
        if (extendsBound != null) {
            return WildcardType.withExtendsBound(extendsBound);
        } else if (superBound != null) {
            return WildcardType.withSuperBound(superBound);
        } else {
            return WildcardType.create();
        }
    }

    @Override
    public DeclaredType getDeclaredType(final TypeElement typeElem,
                                        final TypeMirror... typeArgs) {
        validateTypeArgsCount(typeElem, typeArgs);

        final var classSymbol = (ClassSymbol) typeElem;
        final var isNullable = typeElem.asType().isNullable();

        if (classSymbol.asType() instanceof ErrorType) {
            return new ErrorTypeImpl(
                    classSymbol,
                    List.of(),
                    List.of(typeArgs),
                    isNullable
            );
        } else {
            return new ClassType(
                    classSymbol,
                    List.of(),
                    List.of(typeArgs),
                    isNullable
            );
        }
    }

    private void validateTypeArgsCount(final TypeElement typeElement,
                                       final TypeMirror... typeArgs) {
        /*
        final var typeParameterCount = typeElement.getTypeParameters().size();

        if (typeArgs.length != typeParameterCount) {
            throw new InvalidTypeArgumentsCountException(typeParameterCount, typeArgs.length);
        }
        */
    }

    @Override
    public DeclaredType getDeclaredType(final DeclaredType containing,
                                        final TypeElement typeElem,
                                        final TypeMirror... typeArgs) {
        validateTypeArgsCount(typeElem, typeArgs);
        throw new UnsupportedOperationException();
    }

    @Override
    public io.github.potjerodekool.codegen.model.type.DeclaredType asMemberOf(final DeclaredType containing,
                                                                              final Element element) {
        throw new UnsupportedOperationException();
    }

}

