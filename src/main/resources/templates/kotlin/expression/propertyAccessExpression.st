propertyAccessExpression(propertyAccessExpression) ::= <%
    <if(propertyAccessExpression.target)>
    </kotlin/expression/expression(propertyAccessExpression.target)>.
    <endif>
    <propertyAccessExpression.name>
%>