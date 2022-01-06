package com.justinblank.classcompiler.lang;

public class ArraySet {

    public final Expression arrayRef;
    public final Expression index;

    private ArraySet(Expression arrayRef, Expression index) {
        this.arrayRef = arrayRef;
        this.index = index;
    }
}
