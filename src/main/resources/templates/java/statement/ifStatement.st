ifStatement(ifStatement) ::= <%
if (</java/expression/expression(ifStatement.condition)>) {
<ifStatement.thenStatement.statements:{statement | </java/statement/statement(statement)>}>
}
%>