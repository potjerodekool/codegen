expression(expression) ::= <%
    <if(expression.kind.isTYPE)>
    </java/type/type(expression)>
    <elseif(expression.kind.isBINARY)>
    </java/expression/binaryExpression(expression)>
    <elseif(expression.kind.isUNARY)>
    </java/expression/unaryExpression(expression)>
    <elseif(expression.kind.isLITERAL)>
    </java/expression/literalExpression(expression)>
    <elseif(expression.kind.isIDENTIFIER)>
    </java/expression/indentifierExpression(expression)>
    <elseif(expression.kind.isMETHOD_INVOCATION)>
    </java/expression/methodInvocationExpression(expression)>
    <elseif(expression.kind.isARRAY)>
    </java/expression/arrayExpression(expression)>
    <elseif(expression.kind.isFIELD_ACCESS)>
    </java/expression/fieldAccessExpression(expression)>
    <elseif(expression.kind.isNEW_CLASS)>
    </java/expression/newClassExpression(expression)>
    <elseif(expression.kind.isANNOTATION)>
    </java/element/annotation(expression)>
    <elseif(expression.kind.isWILDCARD)>
    </java/expression/wildCardTypeExpr(expression)>
    <else>
    unknown expression <expression.kind>
    <endif>
%>