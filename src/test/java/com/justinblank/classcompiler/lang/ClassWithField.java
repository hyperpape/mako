package com.justinblank.classcompiler.lang;

public class ClassWithField {

    public final int i = 9;

    public static ClassWithField create() {
        return new ClassWithField();
    }
}
