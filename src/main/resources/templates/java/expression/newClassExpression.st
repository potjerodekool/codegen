newClassExpression(newClassExpression) ::= <%
    new <newClassExpression.name>(
    <newClassExpression.arguments:{argument | </java/expression/expression(argument)>};separator=", ">
    )
%>