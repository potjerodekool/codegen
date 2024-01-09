interface(interface) ::= <<
<interface.annotations:{a| </java/element/annotation(a)>};separator="\n">
</java/modifiers(interface.modifiers)>interface <interface.simpleName> {
<interface.enclosedElements:{e |
<\t></java/element/element(e)>
};separator="\n">
}
>>
