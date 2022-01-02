package com.justinblank.classcompiler.lang;

public class VariableRead extends Expression {

    public final String variable;

    public VariableRead(String variable) {
        this.variable = variable;
    }
}
