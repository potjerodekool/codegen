methodInvocationExpression(methodInvocation) ::= <%
    <if(methodInvocation.target)>
    </kotlin/expression/expression(methodInvocation.target)>.
    <endif>
    <methodInvocation.name>(
    <methodInvocation.arguments:{argument | </kotlin/expression/expression(argument)>};separator=", ">
    )
%>