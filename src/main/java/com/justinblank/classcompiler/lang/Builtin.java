package com.justinblank.classcompiler.lang;

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
}
