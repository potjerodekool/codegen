varDeclarationStatement(varDeclarationStatement) ::= <%
    <if(!varDeclarationStatement.modifiers.isEmpty)>
    </java/modifiers(varDeclarationStatement.modifiers)>
    <\ >
    <endif>
    </java/expression/expression(varDeclarationStatement.type)>
    <\ >
    <varDeclarationStatement.identifiers:{identifier | <identifier>}>
    <if(varDeclarationStatement.initExpression)>
    <\ >= </java/expression/expression(varDeclarationStatement.initExpression)>
    <endif>
%>