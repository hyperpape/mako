package com.justinblank.classcompiler.lang;

public class ArrayRead extends Expression {

    public final Expression arrayRef;
    public final Expression index;

    public ArrayRead(Expression arrayRef, Expression index) {
        this.arrayRef = arrayRef;
        this.index = index;
    }
}
