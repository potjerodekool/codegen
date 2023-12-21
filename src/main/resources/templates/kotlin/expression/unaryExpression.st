unaryExpression(unaryExpression) ::= <%
    <if(unaryExpression.prefix)>
    <unaryExpression.operator>
    <endif>
    </kotlin/expression/expression(unaryExpression.expression)>

    <if(!unaryExpression.prefix)>
    <unaryExpression.operator>
    <endif>
%>