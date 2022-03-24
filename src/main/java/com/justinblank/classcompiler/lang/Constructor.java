package com.justinblank.classcompiler.lang;

public class Constructor implements Expression {

    public final Type returnType;
    public final Expression[] arguments;

    public Constructor(Type returnType, Expression[] arguments) {
        this.returnType = returnType;
        this.arguments = arguments;
    }
}
