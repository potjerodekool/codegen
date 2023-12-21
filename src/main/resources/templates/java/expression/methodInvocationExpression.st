methodInvocationExpression(methodInvocation) ::= <%
    <if(methodInvocation.target)>
    </java/expression/expression(methodInvocation.target)>.
    <endif>
    <methodInvocation.name>(
    <methodInvocation.arguments:{argument | </java/expression/expression(argument)>};separator=", ">
    )
%>