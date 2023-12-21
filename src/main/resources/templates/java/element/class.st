class(class) ::= <<
<class.annotations:{a| </java/element/annotation(a)>}>
</java/modifiers(class.modifiers)> class <class.simpleName> {

<class.enclosedElements:{e |
    </java/element/element(e)>
};separator="\n">
}
>>
