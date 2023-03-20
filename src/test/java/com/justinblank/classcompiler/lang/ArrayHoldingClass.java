package com.justinblank.classcompiler.lang;

public class ArrayHoldingClass {

    public byte[] a = new byte[10];
    public byte[] b = new byte[10];

    public ArrayHoldingClass() {
        for (int i = 0; i < 10; i++) {
            a[i] = (byte) i;
            b[i] = (byte) (10 - i);
        }
    }
}
