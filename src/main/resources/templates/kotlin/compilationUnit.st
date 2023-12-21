compilationUnit(compilationUnit) ::= <<
<if(compilationUnit.packageName)>
package <compilationUnit.packageName>

<endif>
<if(compilationUnit.imports)>
<compilationUnit.imports:{importItem| import <importItem>};separator="\n">

<endif>
<compilationUnit.elements:{e| </kotlin/element/element(e)>}>
>>