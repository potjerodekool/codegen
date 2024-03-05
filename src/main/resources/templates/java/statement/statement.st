statement(statement) ::= <%
<if(statement.kind.isBLOCK)>
<\t><statement.statements:{subStatement | </java/statement/statement(subStatement)>};separator="\n">
<elseif(statement.kind.isVARIABLE_DECLARATION)>
<\t></java/statement/varDeclarationStatement(statement)>;
<elseif(statement.kind.isIF)>
<\t></java/statement/ifStatement(statement)>
<elseif(statement.kind.isRETURN)>
<\t></java/statement/returnStatement(statement)>;
<elseif(statement.kind.isSTATEMENT_EXPRESSION)>
<\t></java/statement/statementExpresion(statement)>;
<else>
<\t>unnknown statement <statement.stateKind>
<endif>
%>