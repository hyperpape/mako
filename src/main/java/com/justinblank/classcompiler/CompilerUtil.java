package com.justinblank.classcompiler;

import com.justinblank.classcompiler.lang.*;
import com.justinblank.classcompiler.lang.Void;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.MethodVisitor;

import java.util.List;

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
            return getTypeString((Builtin) type);
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
        else if (type instanceof Void) {
            return "V";
        }
        else {
            throw new IllegalStateException("Got an unrecognized type while trying to determine descriptor:" + type);
        }
    }

    public static String descriptor(List<String> arguments, String returnType) {
        return "(" + StringUtils.join(arguments, "") + ")" + returnType;
    }

    public static int returnForType(String string) {
        switch (string) {
            case "I":
            case "B":
            case "Z":
            case "C":
                return IRETURN;
            case "J":
                return LRETURN;
            case "F":
                return FRETURN;
            case "D":
                return DRETURN;
            case "()":
                return RETURN;
            default:
                return ARETURN;
        }
    }

    private static String getTypeString(Builtin type) {
        switch (type) {
            case I:
                return "I";
            case F:
                return "F";
            case L:
                return "J";
            case D:
                return "D";
            case OCTET:
                return "B";
            case BOOL:
                return "Z";
            case C:
                return "C";
            default:
                throw new IllegalStateException("Unhandled primitive");
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
                return descriptor(resolved);
            }
            throw new TypeInference.TypeCheckException("Unresolved typeVar does not have descriptor");
        }
    }

    public static String descriptor(String className) {
        className = className.replaceAll("\\.", "/");
        return "L" + className + ";";
    }

    public static String internalName(Class<?> cls) {
        return cls.getCanonicalName().replaceAll("\\.", "/");
    }

    public static String internalName(Type type) {
        if (type instanceof Builtin) {
            return getTypeString((Builtin) type);
        }
        else if (type instanceof ReferenceType) {
            return internalName(((ReferenceType) type).typeString);
        }
        else if (type instanceof ArrayType) {
            return "[" + internalName(((ArrayType) type).elementType);
        }
        else if (type instanceof TypeVariable) {
            return internalName(type.type());
        }
        else {
            throw new IllegalStateException("Unhandled variant:" + type);
        }
    }

    public static String extractDiscriptorInnards(String s) {
        if (s.startsWith("L") && s.endsWith(";")) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }

    public static String internalName(String typeString) {
        return typeString.replaceAll("\\.", "/");
    }

    public static String internalNameToCanonicalName(String internalName) {
        return internalName.replaceAll("/", ".");
    }

    public static String descriptorToCanonicalName(String descriptor) {
        if (descriptor.charAt(0) == 'L') {
            return internalNameToCanonicalName(descriptor.substring(1));
        }
        return descriptor;
    }
}
