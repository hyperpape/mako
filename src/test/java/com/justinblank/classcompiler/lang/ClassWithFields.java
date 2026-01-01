package com.justinblank.classcompiler.lang;

public class ClassWithFields {

    public int i = 9;
    public short s = 5;
    public double d = 2.5;
    public float f = 2.6f;
    public boolean b = false;
    public String string = null;

    public static ClassWithFields create() {
        return new ClassWithFields();
    }
}
