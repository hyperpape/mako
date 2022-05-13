package com.justinblank.classcompiler;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public interface Vars {

    /**
     * Get the index of a variable used in this method
     *
     * @param name the name of the variable
     * @return the index of the variable in this method. Throws {@link IllegalStateException} if the variable is not
     * contained in this method
     */
    int indexByName(String name);

    /**
     * Get a list of all name-index pairs for variabels used in this method
     *
     * @return list of all name-index pairs for variabels used in this method
     */
    List<Pair<String, Integer>> allVars();
}
