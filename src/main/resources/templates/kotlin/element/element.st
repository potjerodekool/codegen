element(element) ::= <%
<if(element.kind.isCLASS)>
</kotlin/element/class(element)>
<elseif(element.kind.isINTERFACE)>
</kotlin/element/interface(element)>
<elseif(element.kind.isCONSTRUCTOR)>
</kotlin/element/method(element)>
<elseif(element.kind.isMETHOD)>
</kotlin/element/method(element)>
<elseif(element.kind.isOBJECT)>
</kotlin/element/object(element)>
<else>
<element.kind>
<endif>
%>