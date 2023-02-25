package com.justinblank.classcompiler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Operation {

    public final Inst inst;
    public final int count;
    // TODO: ugh, just ugh...refactor
    protected final Number number;
    public Block target;
    final List<Block> blockTargets;
    final RefSpec spec;
    final List<Integer> ints;
    final Map<String, Object> attributes = new HashMap<>();

    public static Operation mkReadChar() {
        return new Operation(Inst.READ_CHAR, -1, null, null, null);
    }

    @Override
    public String toString() {
        return "Operation{" +
                "tag=" + inst +
                ", count=" + count +
                ", target='" + target + '\'' +
                '}';
    }

    protected Operation(Operation.Inst inst, Number count, Block blockTarget, RefSpec spec, List<Integer> ints) {
        this.inst = inst;
        if (count instanceof Integer) {
            this.count = (Integer) count;
            this.number = null;
        }
        else {
            this.count = -1;
            number = count;
        }
        this.target = blockTarget;
        this.spec = spec;
        this.ints = ints;
        this.blockTargets = null;
    }


    // We can derive the max from the min, but this is super gross
    protected Operation(Operation.Inst inst, List<Block> targets, Block failTarget, int min) {
        this.inst = inst;
        this.count = min;
        this.target = failTarget;
        this.spec = null;
        this.ints = null;
        this.blockTargets = targets;
        this.number = null;
    }

    public static Operation checkBounds(Block returnBlock) {
        return new Operation(Inst.CHECK_BOUNDS, -1, returnBlock, null, null);
    }

    public static Operation mkJump(Block target, int insn) {
        return new Operation(Inst.JUMP, insn, target, null, null);
    }

    public static Operation mkCallState(Block targetBlock) {
        return new Operation(Inst.CALL_STATE, 0, targetBlock, null, null);
    }

    public static Operation call(String methodName, String className, String descriptor) {
        return call(methodName, className, descriptor, false);
    }

    public static Operation call(String methodName, String className, String descriptor, boolean invokeSpecial) {
        var spec = new RefSpec(methodName, className, descriptor);
        var inst = invokeSpecial ? Inst.INVOKESPECIAL : Inst.CALL;
        return new Operation(inst, -1, null, spec, null);
    }

    public static Operation callStatic(String methodName, String className, String descriptor) {
        var spec = new RefSpec(methodName, className, descriptor);
        var inst = Inst.INVOKESTATIC;
        return new Operation(inst, -1, null, spec, null);
    }

    public static Operation callInterface(String methodName, String className, String descriptor) {
        var spec = new RefSpec(methodName, className, descriptor);
        var inst = Inst.INVOKEINTERFACE;
        return new Operation(inst, -1, null, spec, null);
    }

    public static Operation pushValue(int val) {
        return new Operation(Inst.VALUE, val, null, null, null);
    }

    public static Operation pushValue(float val) {
        return new Operation(Inst.VALUE, val, null, null, null);
    }

    public static Operation pushValue(double val) {
        return new Operation(Inst.VALUE, val, null, null, null);
    }

    public static Operation pushValue(long val) {
        return new Operation(Inst.VALUE, val, null, null, null);
    }

    public static Operation mkReadThis() {
        var spec = new RefSpec(null, null, "", true);
        return new Operation(Inst.READ_VAR, 0, null, spec, null);
    }

    public static Operation mkReadVar(Vars vars, String name, String descriptor) {
        int index = vars.indexByName(name);
        var spec = new RefSpec(name, null, descriptor);
        return new Operation(Inst.READ_VAR, index, null, spec, null);
    }

    public static Operation mkReadField(String field, boolean isSelf, String descriptor) {
        var spec = new RefSpec(field, null, descriptor, true);
        return new Operation(Inst.READ_FIELD, 0, null, spec, null);
    }

    public static Operation mkReadStatic(String field, boolean isSelf, String descriptor) {
        var spec = new RefSpec(field, null, descriptor, isSelf);
        return new Operation(Inst.READ_STATIC, -1, null, spec, null);
    }

    public static Operation mkReadStatic(String field, String className, String descriptor) {
        var spec = new RefSpec(field, className, descriptor, false);
        return new Operation(Inst.READ_STATIC, -1, null, spec, null);
    }

    public static Operation mkPutStatic(String field, boolean isSelf, String descriptor) {
        var spec = new RefSpec(field, null, descriptor, isSelf);
        return new Operation(Inst.PUT_STATIC, -1, null, spec, null);
    }

    public static Operation mkReadField(String field, String className, String descriptor) {
        var spec = new RefSpec(field, className, descriptor);
        return new Operation(Inst.READ_FIELD, -1, null, spec, null);
    }

    public static Operation mkSetField(String field, String className, String descriptor) {
        var spec = new RefSpec(field, className, descriptor);
        return new Operation(Inst.SET_FIELD, -1, null, spec, null);
    }

    public static Operation mkReadVar(int index, String descriptor) {
        var spec = new RefSpec(null, null, descriptor);
        return new Operation(Inst.READ_VAR, index, null, spec, null);
    }

    public static Operation mkSetVar(int stateVar, String descriptor) {
        var spec = new RefSpec(null, null, descriptor);
        return new Operation(Inst.SET_VAR, stateVar, null, spec, null);
    }

    public static Operation mkSetVar(Vars vars, String varName, String descriptor) {
        var index = vars.indexByName(varName);
        var spec = new RefSpec(varName, null, descriptor);
        return new Operation(Inst.SET_VAR, index, null, spec, null);
    }

    public static Operation mkIncrement(String varName, int increment) {
        return new Operation(Inst.INCREMENT, increment, null, new RefSpec(varName, null, "I"), null);
    }

    public static Operation mkReturn(int i) {
        return new Operation(Inst.RETURN, i, null, null, null);
    }

    static Operation mkOperation(Inst inst) {
        return new Operation(inst, -1, null, null, null);
    }

    public static Operation mkOperation(int i) {
        return new Operation(Inst.PASSTHROUGH, i, null, null, null);
    }

    static Operation mkConstructor(String type) {
        // TODO...type is a non-descriptor here?
        var spec = new RefSpec("dummy", "dummy", type);
        return new Operation(Inst.NEW, -1, null, spec, null);
    }

    public static Operation mkTableSwitch(List<Block> blocks, Block failTarget, int min, int max) {
        return new Operation(Inst.TABLESWITCH, blocks, failTarget, min);
    }

    static Operation mkLookupSwitch(List<Block> blocks, Block failTarget) {
        return new Operation(Inst.LOOKUPSWITCH, blocks, failTarget, 0);
    }

    public void addAttribute(String key, Object attr) {
        this.attributes.put(key, attr);
    }

    public Object getAttribute(String key) {
        return this.attributes.get(key);
    }

    public enum Inst {
        VALUE,
        READ_CHAR,
        INCREMENT,
        CHECK_BOUNDS,
        CHECK_CHARS,
        SET_VAR,
        SET_FIELD,
        READ_STATIC,
        PUT_STATIC,
        READ_FIELD,
        READ_VAR,
        PASSTHROUGH,
        JUMP,
        LOOKUPSWITCH,
        TABLESWITCH,
        CALL,
        NEW,
        NEWARRAY,
        INVOKEINTERFACE,
        INVOKESTATIC,
        INVOKESPECIAL,
        CALL_STATE,
        RETURN,
    }
}
