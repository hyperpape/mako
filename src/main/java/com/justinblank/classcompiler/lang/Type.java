package com.justinblank.classcompiler.lang;

public interface Type {

    static Type of(Class<?> paramType) {
        if (paramType.equals(int.class)) {
            return Builtin.I;
        }
        else if (paramType.equals(boolean.class)) {
            return Builtin.BOOL;
        }
        else {
            return ReferenceType.of(paramType.getCanonicalName());
        }
    }

    Type type();

    String typeString();
}
