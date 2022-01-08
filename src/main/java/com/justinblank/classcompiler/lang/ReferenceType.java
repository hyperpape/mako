package com.justinblank.classcompiler.lang;

public class ReferenceType implements Type {

    public Type type() {
        return this;
    }

    public final String typeString;

    ReferenceType(String type) {
        this.typeString = type;
    }

    public static ReferenceType of(Class<?> clz) {
        return of(clz.getCanonicalName());
    }

    public static ReferenceType of(String className) {
        return new ReferenceType(className);
    }
}
