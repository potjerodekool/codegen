element(element) ::= <%
<if(element.kind.isCLASS)>
</java/element/class(element)>
<elseif(element.kind.isINTERFACE)>
</java/element/interface(element)>
<elseif(element.kind.isCONSTRUCTOR)>
</java/element/method(element)>
<elseif(element.kind.isMETHOD)>
</java/element/method(element)>
<else>
<element.kind>
<endif>
%>