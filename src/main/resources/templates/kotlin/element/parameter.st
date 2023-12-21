parameter(parameter) ::= <%
    </kotlin/modifiers(parameter.modifiers)>
    <if(!parameter.modifiers.isEmpty)>
    <\ >
    <endif>
    <parameter.simpleName> : </kotlin/expression/expression(parameter.type)>
%>