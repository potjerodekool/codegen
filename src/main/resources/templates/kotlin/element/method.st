method(method) ::= <%
    </kotlin/modifiers(method.modifiers)>
    <if(!method.modifiers.isEmpty)>
    <\ >
    <endif>
    fun<\ >
    <method.simpleName>(<method.parameters:{ parameter | </kotlin/element/parameter(parameter)>};separator=", ">)
    <if(method.returnType)>
        : </kotlin/expression/expression(method.returnType)>
    <endif>
    <if(method.isAbstract)>
    ;
    <else>
    <\ >
    {
    <if(method.body)>
    <method.body.statements:{statement | </kotlin/statement/statement(statement)>}>
    <endif>
    }
    <endif>



%>