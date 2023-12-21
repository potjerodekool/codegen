expression(expression) ::= <%
    <if(expression.kind.isCLASS_OR_INTERFACE)>
    </java/expression/classOrInterfaceExpression(expression)>
    <elseif(expression.kind.isTYPE)>
    </java/expression/classOrInterfaceExpression(expression)>
    <elseif(expression.kind.isBINARY)>
    </java/expression/binaryExpression(expression)>
    <elseif(expression.kind.isUNARY)>
    </java/expression/unaryExpression(expression)>
    <elseif(expression.kind.isSTRING_LITERAL)>
    </java/expression/stringLiteralExpression(expression)>
    <elseif(expression.kind.isIDENTIFIER)>
    </java/expression/indentifierExpression(expression)>
    <elseif(expression.kind.isMETHOD_INVOCATION)>
    </java/expression/methodInvocationExpression(expression)>
    <elseif(expression.kind.isARRAY)>
    </java/expression/arrayExpression(expression)>
    <else>
    unknown expression <expression.kind>
    <endif>
%>