object(object) ::= <<
<object.annotations:{a| </kotlin/element/annotation(a)>}>
<if(!object.modifiers.isEmpty)></kotlin/modifiers(object.modifiers)> <endif>object <object.simpleName> {

<object.enclosedElements:{e |
    </kotlin/element/element(e)>
};separator="\n">
}
>>
