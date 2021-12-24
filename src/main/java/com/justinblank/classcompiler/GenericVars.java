package com.justinblank.classcompiler;

import java.util.HashMap;
import java.util.Map;

public class GenericVars implements Vars {

    private final Map<String, Integer> vars = new HashMap<>();

    @Override
    public int indexByName(String name) {
        Integer index = vars.get(name);
        if (index == null) {
            throw new IllegalStateException("Tried to get index for nonexistant Var=" + name);
        }
        return index;
    }

    public void addVar(String name) {
        vars.put(name, vars.size() + 1);
    }
}
