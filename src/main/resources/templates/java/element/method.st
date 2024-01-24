method(method) ::= <%
    <method.annotations:{a|</java/element/annotation(a)>};separator="\n">
    </java/modifiers(method.modifiers)>
    <if(!method.modifiers.isEmpty)>
    <\ >
    <endif>
    <if(!method.kind.isCONSTRUCTOR)>
    </java/expression/expression(method.returnType)>
        <\ >
    <endif>
    <method.simpleName>(<method.parameters:{ parameter | </java/element/parameter(parameter)>};separator=", ">)
    <if(method.isAbstract)>
    ;
    <elseif(method.body)>
    <\ >
    {
    <\n>
    <method.body.statements:{statement | <\t></java/statement/statement(statement)>};separator="\n">
    <\n>
    <\t>}
    <else>
    ;
    <endif>
%>