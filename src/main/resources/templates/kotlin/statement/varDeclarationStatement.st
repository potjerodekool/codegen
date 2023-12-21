varDeclarationStatement(varDeclarationStatement) ::= <%
    <if(!varDeclarationStatement.modifiers.isEmpty)>
    </kotlin/modifiers(varDeclarationStatement.modifiers)><\ >
    <endif>

    <varDeclarationStatement.identifiers:{identifier | <identifier>}>

    <if(varDeclarationStatement.initExpression)>
    <\ >= </kotlin/expression/expression(varDeclarationStatement.initExpression)>
    <endif>
%>