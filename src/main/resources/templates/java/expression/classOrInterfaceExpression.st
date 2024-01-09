classOrInterfaceExpression(classOrInterfaceExpression) ::= <%
    <classOrInterfaceExpression.name>
    <if(classOrInterfaceExpression.typeArguments)>
    \<<classOrInterfaceExpression.typeArguments:{typeArgument | </java/expression/expression(typeArgument)>};separator=", ">>
    <endif>
%>