package com.justinblank.classcompiler.lang;

public class ClassWithField {

    public int i = 9;

    public static ClassWithField create() {
        return new ClassWithField();
    }
}
