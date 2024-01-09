literalExpression(literalExpression) ::= <%
    <if(literalExpression.value)>
    <literalExpression.value>
    <else>
    null
    <endif>
%>