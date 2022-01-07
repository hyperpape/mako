package com.justinblank.classcompiler.lang;

public class ArrayRead extends Expression {

    public final Expression arrayRef;
    public final Expression index;

    private ArrayRead(Expression arrayRef, Expression index) {
        this.arrayRef = arrayRef;
        this.index = index;
    }

    public static Expression arrayRead(Expression arrayRef, Expression index) {
        return new ArrayRead(arrayRef, index);
    }
}
