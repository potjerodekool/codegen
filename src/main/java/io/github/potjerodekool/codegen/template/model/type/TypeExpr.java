package io.github.potjerodekool.codegen.template.model.type;

import io.github.potjerodekool.codegen.model.type.TypeKind;
import io.github.potjerodekool.codegen.template.model.expression.Expr;

public interface TypeExpr extends Expr {

    TypeKind getTypeKind();

}
