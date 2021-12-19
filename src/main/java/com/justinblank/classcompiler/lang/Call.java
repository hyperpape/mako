package com.justinblank.classcompiler.lang;

public class Call extends Expression {

    public final String methodName;
    public final Expression[] arguments;

    public Call(String methodName, Expression... arguments) {
        this.methodName = methodName;
        this.arguments = arguments;
    }
}
