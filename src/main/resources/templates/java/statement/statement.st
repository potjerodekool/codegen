statement(statement) ::= <<
<if(statement.kind.isBLOCK)>
    <statement.statements:{subStatement |   </java/statement/statement(subStatement)>}>
<elseif(statement.kind.isVARIABLE_DECLARATION)>
    </java/statement/varDeclarationStatement(statement)>;
<elseif(statement.kind.isIF)>
    </java/statement/ifStatement(statement)>
<elseif(statement.kind.isRETURN)>
    </java/statement/returnStatement(statement)>;
<elseif(statement.kind.isSTATEMENT_EXPRESSION)>
    </java/statement/statementExpresion(statement)>;
<else>
    unnknown statement <statement.kind>
<endif>
>>