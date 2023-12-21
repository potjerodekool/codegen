parameter(parameter) ::= <%
    </java/modifiers(parameter.modifiers)>
    <if(!parameter.modifiers.isEmpty)>
    <\ >
    <endif>
    </java/expression/expression(parameter.type)> <parameter.simpleName>
%>