ifStatement(ifStatement) ::= <%
if (</kotlin/expression/expression(ifStatement.condition)>) {
<ifStatement.thenStatement.statements:{statement | </kotlin/statement/statement(statement)>}>
}
%>