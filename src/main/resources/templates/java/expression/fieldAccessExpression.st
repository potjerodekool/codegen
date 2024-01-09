fieldAccessExpression(fieldAccessExpression) ::= <%
<if(fieldAccessExpression.target)>
</java/expression/expression(fieldAccessExpression.target)>
.
<endif>
</java/expression/expression(fieldAccessExpression.field)>
%>