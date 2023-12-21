unaryExpression(unaryExpression) ::= <%
    <if(unaryExpression.prefix)>
    <unaryExpression.operator>
    <endif>
    </java/expression/expression(unaryExpression.expression)>

    <if(!unaryExpression.prefix)>
    <unaryExpression.operator>
    <endif>
%>