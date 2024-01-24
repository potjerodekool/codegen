interface(interface) ::= <<
<interface.annotations:{a| </java/element/annotation(a)>};separator="\n">
<if(interface.modifiers)></java/modifiers(interface.modifiers)><\ ><endif>interface <interface.simpleName> {
<interface.enclosedElements:{e |
<\t></java/element/element(e)>
};separator="\n">
}
>>
