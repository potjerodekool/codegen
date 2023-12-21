varDeclarationStatement(varDeclarationStatement) ::= <%
<varDeclarationStatement.modifiers:{ modifier | <java/modifier(modifier)>}>
    <if(!varDeclarationStatement.modifiers.isEmpty)>
    mods <\ >
    <endif>
    </java/expression/expression(varDeclarationStatement.type)>
    <\ >
    <varDeclarationStatement.identifiers:{identifier | <identifier>}>
    <if(varDeclarationStatement.initExpression)>
    <\ >= </java/expression/expression(varDeclarationStatement.initExpression)>
    <endif>
%>