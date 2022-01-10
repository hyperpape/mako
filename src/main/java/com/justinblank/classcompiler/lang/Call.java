package com.justinblank.classcompiler.lang;

public class Call implements Expression {

    public final String methodName;
    public final Expression[] arguments;
    public final Type returnType;
    public final boolean isStatic;
    public final String className;

    Call(String className, String methodName, Type returnType, boolean isStatic, Expression... arguments) {
        this.className = className;
        this.methodName = methodName;
        this.arguments = arguments;
        this.returnType = returnType;
        this.isStatic = isStatic;
    }

    public Expression receiver() {
        return this.arguments[0];
    }
}
