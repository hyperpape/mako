package com.justinblank.classcompiler;

import org.objectweb.asm.Label;

import java.util.*;

public class Block {

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

    public Block newArray(int arrayType) {
        addOperation(new Operation(Operation.Inst.NEWARRAY, arrayType, null, null, null));
        return this;
    }

    public Block newArray(String arrayType) {
        var spec = new RefSpec(null, null, arrayType);
        addOperation(new Operation(Operation.Inst.NEWARRAY, -1, null, spec, null));
        return this;
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
}
