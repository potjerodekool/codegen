package io.github.potjerodekool.codegen.extension.buildin;

import io.github.potjerodekool.codegen.kotlin.KotlinClasses;
import io.github.potjerodekool.codegen.extension.DefaultValueResolver;
import io.github.potjerodekool.codegen.model.tree.expression.Expression;
import io.github.potjerodekool.codegen.model.tree.expression.LiteralExpression;
import io.github.potjerodekool.codegen.model.tree.expression.MethodCallExpression;
import io.github.potjerodekool.codegen.model.symbol.ClassSymbol;
import io.github.potjerodekool.codegen.model.tree.type.ClassOrInterfaceTypeExpression;
import io.github.potjerodekool.codegen.model.type.ArrayType;
import io.github.potjerodekool.codegen.model.type.DeclaredType;
import io.github.potjerodekool.codegen.model.type.TypeMirror;
import io.github.potjerodekool.codegen.model.type.kotlin.KotlinArrayType;

public class BuildInDefaultValueResolver implements DefaultValueResolver {

    @Override
    public Expression createDefaultValue(final TypeMirror typeMirror) {
        if (typeMirror instanceof DeclaredType declaredType) {
            final var qualifiedName = ((ClassSymbol) declaredType.asElement()).getQualifiedName();
            if ("org.openapitools.jackson.nullable.JsonNullable".equals(qualifiedName.toString())) {
                return new MethodCallExpression(
                        new ClassOrInterfaceTypeExpression("org.openapitools.jackson.nullable.JsonNullable"),
                        "undefined"
                );
            } else if (!typeMirror.isNullable()) {
                final var className = ((ClassSymbol) declaredType.asElement()).getQualifiedName().toString();

                return switch (className) {
                    case KotlinClasses.KOTLIN_STRING -> LiteralExpression.createStringLiteralExpression();
                    case KotlinClasses.KOTLIN_INT -> LiteralExpression.createIntLiteralExpression();
                    case KotlinClasses.KOTLIN_BOOLEAN -> LiteralExpression.createBooleanLiteralExpression();
                    case KotlinClasses.KOTLIN_BYTE -> LiteralExpression.createByteLiteralExpression();
                    case KotlinClasses.KOTLIN_SHORT -> LiteralExpression.createShortLiteralExpression();
                    case KotlinClasses.KOTLIN_CHAR -> LiteralExpression.createCharLiteralExpression();
                    case KotlinClasses.KOTLIN_FLOAT -> LiteralExpression.createFloatLiteralExpression();
                    case KotlinClasses.KOTLIN_DOUBLE -> LiteralExpression.createDoubleLiteralExpression();
                    case KotlinClasses.KOTLIN_LONG -> LiteralExpression.createLongLiteralExpression();
                    default -> null;
                };
            }
        } else if (typeMirror instanceof ArrayType) {
            final var kotlinArrayType = (KotlinArrayType) typeMirror;
            final var componentType = (DeclaredType) kotlinArrayType.getComponentType();
            final var componentTypeName = ((ClassSymbol)componentType.asElement()).getQualifiedName();
            final var methodName = switch (componentTypeName.toString()) {
                case "kotlin.Byte" -> "byteArrayOf";
                case "kotlin.Char" -> "charArrayOf";
                case "kotlin.Short" -> "shortArrayOf";
                case "kotlin.Int" -> "intArrayOf";
                case "kotlin.Long" -> "longArrayOf";
                case "kotlin.Float" -> "floatArrayOf";
                case "kotlin.Double" -> "doubleArrayOf";
                case "kotlin.Boolean" -> "booleanArrayOf";
                default -> "arrayOf";
            };
            return new MethodCallExpression(
                    null,
                    methodName
            );
        }

        return null;
    }
}
