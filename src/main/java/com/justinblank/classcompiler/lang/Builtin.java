package com.justinblank.classcompiler.lang;

// TODO: rename primitive?
public enum Builtin implements Type {
    I,
    F,
    L,
    D,
    BOOL,
    OCTET;
    // TODO: Forgot char, did ye?

    public Type type() {
        return this;
    }

}
