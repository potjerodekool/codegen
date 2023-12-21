statement(statement) ::= <<
<if(statement.kind.isBLOCK)>
    <statement.statements:{subStatement |   </kotlin/statement/statement(subStatement)>}>
<elseif(statement.kind.isVARIABLE_DECLARATION)>
    </kotlin/statement/varDeclarationStatement(statement)>
<elseif(statement.kind.isIF)>
    </kotlin/statement/ifStatement(statement)>
<elseif(statement.kind.isRETURN)>
    </kotlin/statement/returnStatement(statement)>
<elseif(statement.kind.isSTATEMENT_EXPRESSION)>
    </kotlin/statement/statementExpresion(statement)>
<else>
    unnknown statement <statement.kind>
<endif>
>>