package com.justinblank.classcompiler.lang;

public enum Builtin implements Type {
    I,
    F,
    L,
    D,
    BOOL,
    OCTET;

    public Type type() {
        return this;
    }

}
