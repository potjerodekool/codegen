class(class) ::= <<
<class.annotations:{a|</java/element/annotation(a)>};separator="\n">

<if(class.modifiers)></java/modifiers(class.modifiers)><\ ><endif>class <class.simpleName></java/element/implemention(class.implementing)><\ >{

<class.enclosedElements:{e |
<\t></java/element/element(e)>
};separator="\n">
}
>>
