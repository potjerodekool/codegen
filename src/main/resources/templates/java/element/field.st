field(field) ::= <%
<if(field.annotations)>
<field.annotations:{annotation| </java/element/annotation(annotation)>};separator = "\n">
<\n>
<endif>
</java/modifiers(field.modifiers)>
<if(!field.modifiers.isEmpty)>
<\ >
<endif>
</java/expression/expression(field.type)>
<\ >
<field.simpleName>

<if(field.initExpression)>
<\ >= </java/expression/expression(field.initExpression)>
<endif>

;
%>