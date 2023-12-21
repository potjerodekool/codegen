expression(expression) ::= <%
    <if(expression.kind.isCLASS_OR_INTERFACE)>
    </kotlin/expression/classOrInterfaceExpression(expression)>
    <elseif(expression.kind.isTYPE)>
    </java/expression/classOrInterfaceExpression(expression)>
    <elseif(expression.kind.isBINARY)>
    </kotlin/expression/binaryExpression(expression)>
    <elseif(expression.kind.isUNARY)>
    </kotlin/expression/unaryExpression(expression)>
    <elseif(expression.kind.isSTRING_LITERAL)>
    </kotlin/expression/stringLiteralExpression(expression)>
    <elseif(expression.kind.isIDENTIFIER)>
    </kotlin/expression/indentifierExpression(expression)>
    <elseif(expression.kind.isMETHOD_INVOCATION)>
    </kotlin/expression/methodInvocationExpression(expression)>
    <elseif(expression.kind.isARRAY)>
    </kotlin/expression/arrayExpression(expression)>
    <elseif(expression.kind.isPROPERTY_ACCESS)>
    </kotlin/expression/propertyAccessExpression(expression)>
    <else>
    unknown expression <expression.kind>
    <endif>
%>