literalExpression(literalExpression) ::= <%
    <if(literalExpression.isNullLiteral)>
    null
    <else>
    <literalExpression.value>
    <endif>
%>