package com.justinblank.classcompiler.lang;

public class ArraySet extends Statement {

    public final Expression arrayRef;
    public final Expression index;
    public final Expression value;

    private ArraySet(Expression arrayRef, Expression index, Expression value) {
        this.arrayRef = arrayRef;
        this.index = index;
        this.value = value;
    }

    public static ArraySet arraySet(Expression arrayRef, Expression index, Expression value) {
        return new ArraySet(arrayRef, index, value);
    }

    @Override
    public String toString() {
        return arrayRef.toString() + "[" + index + "] = " + value;
    }


}
