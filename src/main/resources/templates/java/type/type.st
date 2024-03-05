type(type)::= <%
<if(type.typeKind.isDECLARED)>
    </java/type/classOrInterfaceExpression(type)>
<elseif(type.typeKind.isARRAY)>
    </java/type/type(type.componentType)>[]
<elseif(type.typeKind.primitive)>
    </java/type/primitiveType(type)>
<elseif(type.typeKind.isVOID)>
    void
<elseif(type.typeKind.isVAR)>
    var
<elseif(type.typeKind.isWILDCARD)>
    </java/type/wildCardType(type)>
<else>
    unknown type <type.typeKind>
<endif>
%>