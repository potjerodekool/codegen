wildCardTypeExpr(wildCardTypeExpr) ::= <%
    <if(wildCardTypeExpr.boundKind.isEXTENDS)>
    ? extends <expression(wildCardTypeExpr.expr)>
    <elseif(wildCardTypeExpr.boundKind.isSUPER)>
    ? super <expression(wildCardTypeExpr.expr)>
    <else>
    wildcard <wildCardTypeExpr.boundKind>
    <endif>
%>