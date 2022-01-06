package com.justinblank.classcompiler.lang;

// TODO: rename primitive?
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
