wildCardType(wildCardType)::= <%
<if(wildCardType.boundKind.isEXTENDS)>
? extends </java/expression/expression(wildCardType.expr)>
<elseif(wildCardType.boundKind.isSUPER)>
? super </java/expression/expression(wildCardType.expr)>
<endif>
%>
