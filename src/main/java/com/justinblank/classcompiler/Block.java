package com.justinblank.classcompiler;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import java.util.*;

import static org.objectweb.asm.Opcodes.*;

public class Block {

    /**
     * The maximum size of an array literal. This value is somewhat arbitrary in a few ways.
     *
     * The maximum size of an array literal is indirectly limited by the max size of a method, which is 65536 bytes.
     * The bytecode for populating an array requires more than one byte per array element. We could do math to make the
     * boundary precise.
     *
     * However, since a method could have multiple array literals, or other contents that make any such bound fuzzy,
     * the boundary is just a reasonably large power of 2.
     */
    public static final int MAX_ARRAY_LITERAL_LENGTH = 8192;

    public static final Block POSTLOOP = new Block(-1, Collections.unmodifiableList(new ArrayList<>()));
    int number;
    List<Operation> operations;
    Label label;

    public Block(int number, List<Operation> operations) {
        this.number = number;
        this.operations = operations;
    }

    public Block operate(int i) {
        addOperation(Operation.mkOperation(i));
        return this;
    }

    public Block readVar(int index, String descriptor) {
        addOperation(Operation.mkReadVar(index, descriptor));
        return this;
    }

    public Block readVar(Vars vars, String varName, String descriptor) {
        addOperation(Operation.mkReadVar(vars, varName, descriptor));
        return this;
    }

    /**
     * Add an operation for reading a static value off of the current class
     *
     * @param field the fieldName
     * @param descriptor the descriptor for the field type, see {@link CompilerUtil#descriptor}.
     * @return this block
     */
    public Block readStatic(String field, String descriptor) {
        addOperation(Operation.mkReadStatic(field, true, descriptor));
        return this;
    }

    /**
     * Add an operation for reading a static value off of the specified class
     *
     * @param field the fieldName
     * @param className the internal name of a class, see {@link CompilerUtil#internalName}.
     * @param descriptor the descriptor for the field type, see {@link CompilerUtil#descriptor}.
     * @return this block
     */
    public Block readStatic(String field, String className, String descriptor) {
        addOperation(Operation.mkReadStatic(field, className, descriptor));
        return this;
    }

    public Block putStatic(String field, boolean isSelf, String descriptor) {
        addOperation(Operation.mkPutStatic(field, isSelf, descriptor));
        return this;
    }

    public Block putStatic(String fieldName, String className, String descriptor) {
        var spec = new RefSpec(fieldName, className, descriptor, false);
        addOperation(new Operation(Operation.Inst.PUT_STATIC, -1, null, spec, null));
        return this;
    }

    public Block setVar(int index, String descriptor) {
        addOperation(Operation.mkSetVar(index, descriptor));
        return this;
    }

    public Block setVar(Vars vars, String varName, String descriptor) {
        addOperation(Operation.mkSetVar(vars, varName, descriptor));
        return this;
    }

    public Block readField(String field, boolean isSelf, String descriptor) {
        addOperation(Operation.mkReadField(field, isSelf, descriptor));
        return this;
    }

    public Block readField(String field, String className, String descriptor) {
        var spec = new RefSpec(field, className, descriptor);
        addOperation(new Operation(Operation.Inst.READ_FIELD, -1, null, spec, null));
        return this;
    }

    public Block setField(String field, String className, String descriptor) {
        addOperation(Operation.mkSetField(field, className, descriptor));
        return this;
    }

    public Block push(int i) {
        addOperation(Operation.pushValue(i));
        return this;
    }

    public Block push(float f) {
        addOperation(Operation.pushValue(f));
        return this;
    }

    public Block push(double d) {
        addOperation(Operation.pushValue(d));
        return this;
    }

    public Block push(long l) {
        addOperation(Operation.pushValue(l));
        return this;
    }

    public Block cmp(Block target, int i) {
        addOperation(Operation.mkJump(target, i));
        return this;
    }

    public Block jump(Block target, int i) {
        addOperation(Operation.mkJump(target, i));
        return this;
    }

    public Block addReturn(int i) {
        addOperation(Operation.mkReturn(i));
        return this;
    }

    public Block readThis() {
        addOperation(Operation.mkReadThis());
        return this;
    }

    public Block call(String methodName, String className, String descriptor) {
        addOperation(Operation.call(methodName, className, descriptor, false));
        return this;
    }

    public Block call(String methodName, String className, String descriptor, boolean invokeSpecial) {
        addOperation(Operation.call(methodName, className, descriptor, invokeSpecial));
        return this;
    }

    public Block callStatic(String methodName, String className, String descriptor) {
        addOperation(Operation.callStatic(methodName, className, descriptor));
        return this;
    }

    public Block callInterface(String methodName, String className, String descriptor) {
        addOperation(Operation.callInterface(methodName, className, descriptor));
        return this;
    }

    public Block construct(String type) {
        addOperation(Operation.mkConstructor(type));
        return this;
    }

    /**
     * Creates a new array. The array type comes from the asm constants, e.g. {@link org.objectweb.asm.Opcodes.T_BOOLEAN}.
     *
     * @param arrayType
     * @return
     */
    public Block newArray(int arrayType) {
        addOperation(new Operation(Operation.Inst.NEWARRAY, arrayType, null, null, null));
        return this;
    }

    public Block newArray(String arrayType) {
        var spec = new RefSpec(null, null, arrayType);
        addOperation(new Operation(Operation.Inst.NEWARRAY, -1, null, spec, null));
        return this;
    }

    protected static void checkMaxArrayInitializerLength(int length) {
        if (length > Block.MAX_ARRAY_LITERAL_LENGTH) {
            throw new IllegalArgumentException("Cannot have a literal array of > " + Block.MAX_ARRAY_LITERAL_LENGTH + " elements");
        }
    }

    protected static void pushInitializedArrayToStack(boolean[] bools, Block block) {
        block.push(bools.length);
        block.newArray(T_BOOLEAN);
        block.addOperation(Operation.mkOperation(Opcodes.DUP));
        for (int i = 0; i < bools.length; i++) {
            block.push(i);
            block.push(bools[i] ? 1 : 0);
            block.addOperation(Operation.mkOperation(Opcodes.BASTORE));
            block.addOperation(Operation.mkOperation(Opcodes.DUP));
        }
    }

    protected static void pushInitializedArrayToStack(int[] ints, Block block) {
        block.push(ints.length);
        block.newArray(T_INT);
        block.addOperation(Operation.mkOperation(Opcodes.DUP));
        for (int i = 0; i < ints.length; i++) {
            block.push(i);
            block.push(ints[i]);
            block.addOperation(Operation.mkOperation(Opcodes.IASTORE));
            block.addOperation(Operation.mkOperation(Opcodes.DUP));
        }
    }

    public static void pushInitializedArrayToStack(long[] longs, Block block) {
        block.push(longs.length);
        block.newArray(T_LONG);
        block.addOperation(Operation.mkOperation(Opcodes.DUP));
        for (int i = 0; i < longs.length; i++) {
            block.push(i);
            block.push(longs[i]);
            block.addOperation(Operation.mkOperation(Opcodes.LASTORE));
            block.addOperation(Operation.mkOperation(Opcodes.DUP));
        }
    }

    public static void pushInitializedArrayToStack(float[] floats, Block block) {
        block.push(floats.length);
        block.newArray(T_FLOAT);
        block.addOperation(Operation.mkOperation(Opcodes.DUP));
        for (int i = 0; i < floats.length; i++) {
            block.push(i);
            block.push(floats[i]);
            block.addOperation(Operation.mkOperation(Opcodes.FASTORE));
            block.addOperation(Operation.mkOperation(Opcodes.DUP));
        }
    }

    public static void pushInitializedArrayToStack(double[] doubles, Block block) {
        block.push(doubles.length);
        block.newArray(T_DOUBLE);
        block.addOperation(Operation.mkOperation(Opcodes.DUP));
        for (int i = 0; i < doubles.length; i++) {
            block.push(i);
            block.push(doubles[i]);
            block.addOperation(Operation.mkOperation(Opcodes.DASTORE));
            block.addOperation(Operation.mkOperation(Opcodes.DUP));
        }
    }

    public static void pushInitializedArrayToStack(char[] chars, Block block) {
        block.push(chars.length);
        block.newArray(T_CHAR);
        block.addOperation(Operation.mkOperation(Opcodes.DUP));
        for (int i = 0; i < chars.length; i++) {
            block.push(i);
            block.push(chars[i]);
            block.addOperation(Operation.mkOperation(Opcodes.CASTORE));
            block.addOperation(Operation.mkOperation(Opcodes.DUP));
        }
    }

    public static void pushInitializedArrayToStack(short[] shorts, Block block) {
        block.push(shorts.length);
        block.newArray(T_SHORT);
        block.addOperation(Operation.mkOperation(Opcodes.DUP));
        for (int i = 0; i < shorts.length; i++) {
            block.push(i);
            block.push(shorts[i]);
            block.addOperation(Operation.mkOperation(Opcodes.SASTORE));
            block.addOperation(Operation.mkOperation(Opcodes.DUP));
        }
    }

    public static void pushInitializedArrayToStack(byte[] bytes, Block block) {
        block.push(bytes.length);
        block.newArray(T_BYTE);
        block.addOperation(Operation.mkOperation(Opcodes.DUP));
        for (int i = 0; i < bytes.length; i++) {
            block.push(i);
            block.push(bytes[i]);
            block.addOperation(Operation.mkOperation(Opcodes.BASTORE));
            block.addOperation(Operation.mkOperation(Opcodes.DUP));
        }
    }

    @Override
    public String toString() {
        return "Block" + number + ", operationCount=" + operations.size();
    }

    public Block addOperation(Operation op) {
        this.operations.add(op);
        return this;
    }

    public Label getLabel() {
        if (label == null) {
            label = new Label();
        }
        return label;
    }

    public boolean isEmpty() {
        return operations.isEmpty();
    }

    protected boolean endsWithReturn() {
        if (this.operations.isEmpty()) {
            return false;
        }
        var lastOperation = this.operations.get(this.operations.size() - 1);
        if (lastOperation.inst == Operation.Inst.RETURN) {
            return true;
        }

        return false;
    }
}
