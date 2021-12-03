package com.justinblank.classcompiler.lang;

public class Call extends Expression {

    private final String methodName;

    public Call(String methodName, Expression... arguments) {
        this.methodName = methodName;
    }
}
