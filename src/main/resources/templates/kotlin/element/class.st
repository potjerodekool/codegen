class(class) ::= <<
<class.annotations:{a| </kotlin/element/annotation(a)>}>
</kotlin/modifiers(class.modifiers)> class <class.simpleName> {

<class.enclosedElements:{e |
    </kotlin/element/element(e)>
};separator="\n">
}
>>
