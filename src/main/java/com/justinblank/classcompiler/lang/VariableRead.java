package com.justinblank.classcompiler.lang;

public class VariableRead extends Expression {

    public final String variable;
    public final Type type;

    public VariableRead(String variable, Type type) {
        this.variable = variable;
        this.type = type;
    }
}
