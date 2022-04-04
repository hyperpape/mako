package com.justinblank.classcompiler.lang;

import java.util.Optional;

import static org.objectweb.asm.Opcodes.*;

// TODO: rename primitive?
public enum Builtin implements Type {
    I(IADD, ISUB, IMUL, IDIV),
    F(FADD, FSUB, FMUL, FDIV),
    L(LADD, LSUB, LMUL, LDIV),
    D(DADD, DSUB, DMUL, DDIV),
    BOOL(-1, -1, -1, -1),
    OCTET(-1, -1, -1, -1);
    // TODO: Forgot char, did ye?

    private final int addOperation;
    private final int subOperation;
    private final int multiplicationOperation;
    private final int divisionOperation;

    Builtin(int addOperation, int subOperation, int multiplicationOperation, int divisionOperation) {
        this.addOperation = addOperation;
        this.subOperation = subOperation;
        this.multiplicationOperation = multiplicationOperation;
        this.divisionOperation = divisionOperation;
    }

    public Type type() {
        return this;
    }

    public int addOperation() {
        if (addOperation > 0) {
            return addOperation;
        }
        throw new IllegalArgumentException("Tried to get an add operation on a type not supporting it" + this.toString());
    }

    public int subtractionOperation() {
        if (subOperation > 0) {
            return subOperation;
        }
        throw new IllegalArgumentException("Tried to get an add operation on a type not supporting it" + this.toString());
    }

    public int multiplicationOperation() {
        if (multiplicationOperation > 0) {
            return multiplicationOperation;
        }
        throw new IllegalArgumentException("Tried to get an add operation on a type not supporting it" + this.toString());
    }

    public int divisionOperation() {
        if (divisionOperation > 0) {
            return divisionOperation;
        }
        throw new IllegalArgumentException("Tried to get an add operation on a type not supporting it" + this.toString());
    }

    public String typeString() {
        switch (this) {
            case L:
                return "J";
            case D:
            case F:
            case I:
                return toString();
            case BOOL:
                return "Z";
            case OCTET:
                return "B";
            default:
                throw new IllegalStateException("Unrecognized Builtin enum member: this error should be impossible");
        }
    }

    /**
     * Return the builtin corresponding to a type, null if it cannot be recognized
     * @param returnType
     * @return
     */
    public static Optional<Builtin> from(String returnType) {
        switch (returnType) {
            case "I":
                return Optional.of(I);
            case "F":
                return Optional.of(F);
            case "D":
                return Optional.of(D);
            case "Z":
                return Optional.of(BOOL);
            case "B":
                return Optional.of(OCTET);
            case "J":
                return Optional.of(L);
            default:
                return Optional.empty();
        }
    }

    public int cast(Builtin other) {
        switch (this) {
            case I:
                switch (other) {
                    case L:
                        return I2L;
                    case F:
                        return I2F;
                    case D:
                        return I2D;
                }
            case F:
                switch (other) {
                    case I:
                        return F2I;
                    case D:
                        return F2D;
                    case L:
                        return F2L;
                }
            case D:
                switch (other) {
                    case I:
                        return D2I;
                    case F:
                        return D2F;
                    case L:
                        return D2L;
                }
            case L:
                switch (other) {
                    case I:
                        return L2I;
                    case F:
                        return L2F;
                    case D:
                        return L2D;
                }
            default:
                return -1;
        }
    }
}
