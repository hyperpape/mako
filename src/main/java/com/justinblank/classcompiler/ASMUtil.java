package com.justinblank.classcompiler;

import org.objectweb.asm.Opcodes;

public class ASMUtil {

    public static int negateJump(int jump) {
        switch (jump) {
            case (Opcodes.GOTO): {
                return Opcodes.GOTO;
            }
            case (Opcodes.IF_ICMPEQ): {
                return Opcodes.IF_ICMPNE;
            }
            case (Opcodes.IF_ICMPNE): {
                return Opcodes.IF_ICMPEQ;
            }
            case (Opcodes.IF_ICMPGT): {
                return Opcodes.IF_ICMPLE;
            }
            case (Opcodes.IF_ICMPGE): {
                return Opcodes.IF_ICMPLT;
            }
            case (Opcodes.IF_ICMPLT): {
                return Opcodes.IF_ICMPGE;
            }
            case (Opcodes.IF_ICMPLE): {
                return Opcodes.IF_ICMPGT;
            }
            case (Opcodes.IFEQ): {
                return Opcodes.IFNE;
            }
            case (Opcodes.IFNE): {
                return Opcodes.IFEQ;
            }
            case (Opcodes.IFLT): {
                return Opcodes.IFGE;
            }
            case (Opcodes.IFLE): {
                return Opcodes.IFGT;
            }
            case (Opcodes.IFGT): {
                return Opcodes.IFLE;
            }
            case (Opcodes.IFGE): {
                return Opcodes.IFLT;
            }
            default: {
                return -1;
            }
        }
    }
}

