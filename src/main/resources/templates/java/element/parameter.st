parameter(parameter) ::= <%
    <parameter.annotations:{a|</java/element/annotation(a)>};separator="\n">
    </java/modifiers(parameter.modifiers)>
    <if(!parameter.modifiers.isEmpty)>
    <\ >
    <endif>
    </java/expression/expression(parameter.type)> <parameter.simpleName>
%>