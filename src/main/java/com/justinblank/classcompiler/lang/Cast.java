package com.justinblank.classcompiler.lang;

import static org.objectweb.asm.Opcodes.*;

public class Cast implements Expression {

    public final Type outputType;
    public final Expression expression;

    Cast(Type outputType, Expression expression) {
        this.outputType = outputType;
        this.expression = expression;
    }

    public int op(Type inputType) {
        if (inputType.type() instanceof Builtin && outputType instanceof Builtin) {
            Builtin in = (Builtin) inputType.type();
            Builtin out = (Builtin) outputType;
            if (in == out) {
                return -1;
            }
            switch (in) {
                case I:
                    switch (out) {
                        case F:
                            return I2F;
                        case L:
                            return I2L;
                        case D:
                            return I2D;
                        default:
                            throw new IllegalArgumentException("Not handled:" + out);
                    }
                case F:
                    switch (out) {
                        case I:
                            return F2I;
                        case D:
                            return F2D;
                        case L:
                            return F2L;
                        default:
                            throw new IllegalArgumentException("Not handled:" + out);
                    }
                case D:
                    switch (out) {
                        case I:
                            return D2I;
                        case F:
                            return D2F;
                        case L:
                            return D2L;
                        default:
                            throw new IllegalArgumentException("Not handled:" + out);
                    }
                case L:
                    switch (out) {
                        case I:
                            return L2I;
                        case D:
                            return L2D;
                        case F:
                            return L2F;
                        default:
                            throw new IllegalArgumentException("Not handled:" + out);
                    }
                default:
                    // TODO
                    throw new IllegalArgumentException("Oy vey, too many casts");
            }
        }
        return -1;
    }
}
