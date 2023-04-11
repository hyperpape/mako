package com.justinblank.classcompiler.lang;

import java.util.List;

import static org.objectweb.asm.Opcodes.*;

public class Cast implements Expression {

    public final Type outputType;
    public final Expression expression;

    Cast(Type outputType, Expression expression) {
        this.outputType = outputType;
        this.expression = expression;
    }

    public List<Integer> op(Type inputType) {
        if (inputType.type() instanceof Builtin && outputType instanceof Builtin) {
            Builtin in = (Builtin) inputType.type();
            Builtin out = (Builtin) outputType;
            if (in == out) {
                return List.of();
            }
            switch (in) {
                case I:
                    switch (out) {
                        case F:
                            return List.of(I2F);
                        case L:
                            return List.of(I2L);
                        case D:
                            return List.of(I2D);
                        case S:
                            return List.of(I2S);
                        default:
                            throw new IllegalArgumentException("Not handled:" + out);
                    }
                case F:
                    switch (out) {
                        case I:
                            return List.of(F2I);
                        case S:
                            return List.of(F2I, I2S);
                        case D:
                            return List.of(F2D);
                        case L:
                            return List.of(F2L);
                        default:
                            throw new IllegalArgumentException("Not handled:" + out);
                    }
                case D:
                    switch (out) {
                        case I:
                            return List.of(D2I);
                        case S:
                            return List.of(D2I, I2S);
                        case F:
                            return List.of(D2F);
                        case L:
                            return List.of(D2L);
                        default:
                            throw new IllegalArgumentException("Not handled:" + out);
                    }
                case L:
                    switch (out) {
                        case I:
                            return List.of(L2I);
                        case S:
                            return List.of(L2I, I2S);
                        case D:
                            return List.of(L2D);
                        case F:
                            return List.of(L2F);
                        default:
                            throw new IllegalArgumentException("Not handled:" + out);
                    }
                default:
                    // TODO
                    throw new IllegalArgumentException("Oy vey, too many casts");
            }
        }
        // TODO: Do I need to warn?
        return List.of();
    }
}
