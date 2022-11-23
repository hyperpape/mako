package com.justinblank.classcompiler.lang;

import com.justinblank.classcompiler.ClassCompilationException;

import java.util.Optional;

import static org.objectweb.asm.Opcodes.*;

// TODO: rename primitive?
public enum Builtin implements Type {
    I(IADD, ISUB, IMUL, IDIV, IREM, -1, -1, IF_ICMPLT, IF_ICMPLE, IF_ICMPGT, IF_ICMPGE),
    F(FADD, FSUB, FMUL, FDIV, FREM, FCMPL, FCMPG, IFGE, IFGT, IFLE, IFLT),
    L(LADD, LSUB, LMUL, LDIV, LREM, LCMP, LCMP, IFGE, IFGT, IFLE, IFLT),
    D(DADD, DSUB, DMUL, DDIV, DREM, DCMPL, DCMPG, IFGE, IFGT, IFLE, IFLT),
    // TODO: check semantics of Bool/Octet, can we add, etc?
    BOOL(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1),
    OCTET(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1),
    C(IADD, ISUB, IMUL, IDIV, IREM, -1, -1, IF_ICMPLT, IF_ICMPLE, IF_ICMPGT, IF_ICMPGE);

    private final int addOperation;
    private final int subOperation;
    private final int multiplicationOperation;
    private final int divisionOperation;
    private final int modOperation;

    private final int comparisonGreater;
    private final int comparisonLesser;
    private final int lessThanOperation;
    private final int lessThanOrEqualsOperation;
    private final int greaterThanOperation;
    private final int greaterThanOrEqualsOperation;

    Builtin(int addOperation, int subOperation, int multiplicationOperation, int divisionOperation, int modOperation, int comparisonGreater, int comparisonLesser, int lessThanOperation, int lessThanOrEqualsOperation, int greaterThanOperation, int greaterThanOrEqualsOperation) {
        this.addOperation = addOperation;
        this.subOperation = subOperation;
        this.multiplicationOperation = multiplicationOperation;
        this.divisionOperation = divisionOperation;
        this.modOperation = modOperation;
        this.comparisonGreater = comparisonGreater;
        this.comparisonLesser = comparisonLesser;
        this.lessThanOperation = lessThanOperation;
        this.lessThanOrEqualsOperation = lessThanOrEqualsOperation;
        this.greaterThanOperation = greaterThanOperation;
        this.greaterThanOrEqualsOperation = greaterThanOrEqualsOperation;
    }

    public Type type() {
        return this;
    }

    public int addOperation() {
        if (addOperation > 0) {
            return addOperation;
        }
        throw new IllegalArgumentException("Tried to get an add operation on a type not supporting it" + this);
    }

    public int subtractionOperation() {
        if (subOperation > 0) {
            return subOperation;
        }
        throw new IllegalArgumentException("Tried to get a subtraction operation on a type not supporting it" + this);
    }

    public int multiplicationOperation() {
        if (multiplicationOperation > 0) {
            return multiplicationOperation;
        }
        throw new IllegalArgumentException("Tried to get a multiplication operation on a type not supporting it" + this);
    }

    public int divisionOperation() {
        if (divisionOperation > 0) {
            return divisionOperation;
        }
        throw new IllegalArgumentException("Tried to get a division on a type not supporting it" + this);
    }

    public int modOperation() {
        if (modOperation > 0) {
            return modOperation;
        }
        throw new IllegalArgumentException("Tried to get a mod operation on a type not supporting it" + this);
    }

    public int comparisonGreater() {
        return comparisonGreater;
    }

    public int comparisonLesser() {
        return comparisonLesser;
    }

    public int lessThanOperation() {
        if (lessThanOperation> 0) {
            return lessThanOperation;
        }
        throw new ClassCompilationException("Tried to get a lessThan operation on a type not supporting it" + this);
    }

    public int greaterThanOperation() {
        if (greaterThanOperation> 0) {
            return greaterThanOperation;
        }
        throw new ClassCompilationException("Tried to get a greaterThan operation on a type not supporting it" + this);
    }

    public int lessThanOrEqualsOperation() {
        if (lessThanOrEqualsOperation > 0) {
            return lessThanOrEqualsOperation;
        }
        throw new ClassCompilationException("Tried to get a lessThanOrEquals operation on a type not supporting it" + this);

    }

    public int greaterThanOrEqualsOperation() {
        if (greaterThanOrEqualsOperation> 0) {
            return greaterThanOrEqualsOperation;
        }
        throw new IllegalArgumentException("Tried to get an add operation on a type not supporting it" + this);
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
     * @param type the type string  
     * @return the builtin type corresponding to the passed string
     */
    public static Optional<Builtin> from(String type) {
        switch (type) {
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
