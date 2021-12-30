package com.justinblank.classcompiler.lang;

public class Call extends Expression {

    public final String methodName;
    public final Expression[] arguments;
    public final Type returnType;

    public Call(String methodName, Type returnType, Expression... arguments) {
        this.methodName = methodName;
        this.arguments = arguments;
        this.returnType = returnType;
    }

    public Expression receiver() {
        return this.arguments[0];
    }
}
