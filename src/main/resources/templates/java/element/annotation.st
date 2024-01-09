annotation(annotation) ::= <<
@<annotation.name>(<annotation.values.keys:{key| <key> = </java/expression/expression(annotation.values.(key))>};separator = ", ">)
>>