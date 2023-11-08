package io.github.potjerodekool.codegen.model.element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NameCache {

    public static final NameCache INSTANCE = new NameCache();

    private final Map<Integer, Object> cacheMap = new HashMap<>();

    public final Name EMPTY;

    private NameCache() {
        this.EMPTY = add(new NameImpl(""));
    }

    public Name findName(final CharSequence charSequence) {
        if (charSequence.isEmpty()) {
            return EMPTY;
        }

        final int hashCode = charSequence.hashCode();
        final var object = cacheMap.get(hashCode);

        if (object == null) {
            return null;
        } else if (object instanceof Name name) {
            return name;
        } else {
            final var names = (List<Name>) object;
            return names.stream()
                    .filter(name -> name.contentEquals(charSequence))
                    .findFirst()
                    .orElse(null);
        }
    }

    public Name add(final Name name) {
        final int hashCode = name.hashCode();
        final var object = cacheMap.get(hashCode);

        if (object == null) {
            cacheMap.put(hashCode, name);
            return name;
        } else if (object instanceof Name existingName) {
            if (existingName.contentEquals(name)) {
                return existingName;
            } else {
                final var list = new ArrayList<Name>();
                list.add(existingName);
                list.add(name);
                return name;
            }
        } else {
            final var list = (List<Name>) object;
            list.add(name);
            return name;
        }
    }

}
