package com.justinblank.classcompiler.lang;

import java.util.Objects;

public class ArraySet extends Statement {

    public final Expression arrayRef;
    public final Expression index;
    public final Expression value;

    private ArraySet(Expression arrayRef, Expression index, Expression value) {
        this.arrayRef = Objects.requireNonNull(arrayRef, "arrayRef cannot be null");
        this.index = Objects.requireNonNull(index, "index cannot be null");
        this.value = Objects.requireNonNull(value, "value cannot be null");
    }

    public static ArraySet arraySet(Expression arrayRef, Expression index, Expression value) {
        return new ArraySet(arrayRef, index, value);
    }

    @Override
    public String toString() {
        return arrayRef.toString() + "[" + index + "] = " + value;
    }


}
