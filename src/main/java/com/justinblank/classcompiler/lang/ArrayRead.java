package com.justinblank.classcompiler.lang;

import static com.justinblank.classcompiler.lang.Literal.literal;

public class ArrayRead implements Expression {

    public final Expression arrayRef;
    public final Expression index;

    private ArrayRead(Expression arrayRef, Expression index) {
        this.arrayRef = arrayRef;
        this.index = index;
    }

    public static Expression arrayRead(Expression arrayRef, Expression index) {
        return new ArrayRead(arrayRef, index);
    }

    public static Expression arrayRead(Expression arrayRef, Integer integer) {
        return arrayRead(arrayRef, literal(integer));
    }

    @Override
    public String toString() {
        return arrayRef.toString() + "[" + index + "]";
    }
}
