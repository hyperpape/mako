package com.justinblank.classcompiler;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    @Override
    public List<Pair<String, Integer>> allVars() {
        var l = new ArrayList<Pair<String, Integer>>();
        for (var i = 0; i < this.vars.size(); i++) {
            l.add(null);
        }
        for (var e : this.vars.entrySet()) {
            // TODO: minus 1 is hacky
            l.set(e.getValue() - 1, Pair.of(e.getKey(), e.getValue()));
        }
        return l;
    }
}
