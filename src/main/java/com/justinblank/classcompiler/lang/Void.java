package com.justinblank.classcompiler.lang;

public class Void implements Type {

    public static final Void VOID = new Void();

    private Void() {}

    @Override
    public Type type() {
        return this;
    }

    @Override
    public String typeString() {
        return "V";
    }
}
