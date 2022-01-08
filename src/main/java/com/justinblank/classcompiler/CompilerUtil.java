package com.justinblank.classcompiler;

import com.justinblank.classcompiler.lang.*;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.SIPUSH;

public class CompilerUtil {

    public static final String OBJECT_DESCRIPTOR = "Ljava/lang/Object;";
    public static final String STRING_DESCRIPTOR = "Ljava/lang/String;";

    /**
     * Utility method, push an int onto the stack, assuming it is no bigger than a short
     *
     * @param mv       method visitor
     * @param constant the constant
     */
    public static void pushInt(MethodVisitor mv, int constant) {
        if (constant < 6 && constant >= -1) {
            switch (constant) {
                case -1: {
                    mv.visitInsn(ICONST_M1);
                    break;
                }
                case 0: {
                    mv.visitInsn(ICONST_0);
                    break;
                }
                case 1: {
                    mv.visitInsn(ICONST_1);
                    break;
                }
                case 2: {
                    mv.visitInsn(ICONST_2);
                    break;
                }
                case 3: {
                    mv.visitInsn(ICONST_3);
                    break;
                }
                case 4: {
                    mv.visitInsn(ICONST_4);
                    break;
                }
                case 5: {
                    mv.visitInsn(ICONST_5);
                }
            }
        } else if (constant <= 127 && constant >= 0) {
            mv.visitIntInsn(BIPUSH, constant);
        } else if (constant >= Short.MIN_VALUE && constant <= Short.MAX_VALUE) {
            mv.visitIntInsn(SIPUSH, constant);
        } else {
            mv.visitLdcInsn(constant);
        }
    }

    public static String descriptor(Class<?> cls) {
        return descriptor(cls.getCanonicalName());
    }

    public static String descriptor(Type type) {
        if (type instanceof Builtin) {
            switch ((Builtin) type) {
                case I:
                    return "I";
                case F:
                    return "F";
                case L:
                    return "L";
                case D:
                    return "D";
                case OCTET:
                    return "B";
                case BOOL:
                    return "Z";
                default:
                    throw new IllegalStateException("Unhandled primitive");
            }
        }
        else if (type instanceof ArrayType) {
            return "[" + descriptor(((ArrayType) type).elementType);
        }
        else if (type instanceof ReferenceType) {
            return descriptor(((ReferenceType) type).typeString);
        }
        else if (type instanceof TypeVariable) {
            return descriptor(type.type());
        }
        else {
            throw new IllegalStateException("Got an unrecognized type:" + type);
        }
    }

    public static String descriptorForType(Type type) {
        if (type instanceof Builtin) {
            return type.toString();
        }
        else if (type instanceof ReferenceType) {
            return ((ReferenceType) type).typeString;
        }
        else if (type instanceof ArrayType) {
            return "[" + descriptorForType(((ArrayType) type).elementType);
        }
        else {
            var typeVar = (TypeVariable) type;
            var resolved = typeVar.type();
            if (resolved instanceof Builtin) {
                return resolved.toString();
            }
            if (resolved != null) {
                return descriptor(resolved.toString());
            }
            throw new UnsupportedOperationException("TODO");
        }
    }

    public static String descriptor(String className) {
        className = className.replaceAll("\\.", "/");
        return "L" + className + ";";
    }

    public static String internalName(Class<?> cls) {
        return cls.getCanonicalName().replaceAll("\\.", "/");
    }
}
