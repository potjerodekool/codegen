method(method) ::= <%
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
    <else>
    <\ >
    {
    <if(method.body)>
    <method.body.statements:{statement | </java/statement/statement(statement)>}>
    <endif>
    }
    <endif>
%>