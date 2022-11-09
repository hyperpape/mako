package com.justinblank.classcompiler.lang;

import com.justinblank.classcompiler.CompilerUtil;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.ARETURN;

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

    static Type fromDescriptor(String returnType) {
        switch (returnType) {
            case "I":
                return Builtin.I;
            case "B":
                return Builtin.OCTET;
            case "Z":
                return Builtin.BOOL;
            case "C":
                return Builtin.C;
            case "J": // TODO: wrong?
                return Builtin.L;
            case "F":
                return Builtin.F;
            case "D":
                return Builtin.D;
            case "()":
                return Void.VOID;
            default:
                if (returnType.startsWith("[")) {
                    return ArrayType.of(fromDescriptor(returnType.substring(1)));
                }
                return ReferenceType.of(CompilerUtil.descriptorToCanonicalName(returnType));

        }
    }

    Type type();

    String typeString();

    default boolean resolved() {
        return true;
    }
}
