package com.justinblank.classcompiler;

import com.justinblank.classcompiler.lang.CodeElement;
import com.justinblank.classcompiler.lang.Conditional;
import com.justinblank.classcompiler.lang.Expression;
import com.justinblank.classcompiler.lang.Loop;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

public class Method {

    public final String methodName;
    final int modifiers;
    final List<String> arguments;
    final List<Block> blocks;
    final String returnType;
    private final Vars matchingVars;
    private final Map<String, Object> attributes = new HashMap<>();
    private List<CodeElement> elements = new ArrayList<>();

    public Method(String methodName, List<String> arguments, String returnType, Vars matchingVars) {
        this(methodName, arguments, returnType, matchingVars, ACC_PUBLIC);
    }

    Method(String methodName, List<String> arguments, String returnType, Vars matchingVars, int modifiers) {
        Objects.requireNonNull(methodName);
        Objects.requireNonNull(arguments);
        Objects.requireNonNull(returnType);
        this.modifiers = modifiers;
        this.methodName = methodName;
        this.arguments = arguments;
        this.blocks = new ArrayList<>();
        this.returnType = returnType;
        this.matchingVars = matchingVars;
    }

    public Block addBlock() {
        Block block = new Block(this.blocks.size(), new ArrayList<>());
        this.blocks.add(block);
        return block;
    }

    public void addBlock(Block block) {
        block.number = this.blocks.size();
        this.blocks.add(block);
    }

    /**
     * Create a new block and insert after the passed block
     *
     * @param block the block after which the new block will be inserted
     * @return the new block
     */
    public Block addBlockAfter(Block block) {
        var inserted = new Block(block.number, new ArrayList<>());
        this.blocks.add(block.number + 1, inserted);
        for (int i = block.number + 1; i < blocks.size(); i++) {
            this.blocks.get(i).number++;
        }
        return inserted;
    }

    public Object getAttribute(String s) {
        return attributes.get(s);
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public String descriptor() {
        return "(" + StringUtils.join(arguments, "") + ")" + returnType;
    }

    public Optional<Vars> getMatchingVars() {
        return Optional.ofNullable(matchingVars);
    }

    @Override
    public String toString() {
        return "Method{" +
                "methodName='" + methodName + '\'' +
                ", arguments=" + arguments +
                ", returnType='" + returnType + '\'' +
                ", blockCount=" + blocks.size() +
                '}';
    }

    public int operationCount() {
        int count = 0;
        for (var block : blocks) {
            count += block.operations.size();
        }
        return count;
    }

    @Override
    public int hashCode() {
        return Objects.hash(methodName, modifiers);
    }

    // TODO: fix n-squared
    public void insertBlocks(Block anchor, List<Block> newBlocks) {
        var i = blocks.indexOf(anchor) + 1;
        for (var block : newBlocks) {
            block.number = i;
            blocks.add(i++, block);
            for (var j = i; j < blocks.size(); j++) {
                blocks.get(j).number++;
            }
        }

    }

    public List<Block> getBlocks() {
        return Collections.unmodifiableList(blocks);
    }

    public Conditional cond(Expression condition) {
        var conditional = new Conditional(condition);
        this.elements.add(conditional);
        return conditional;
    }

    public Loop loop(Expression condition, List<CodeElement> body) {
        var loop = new Loop(condition, body);
        this.elements.add(loop);
        return loop;
    }

    public void resolve() {
        for (var element : elements) {
            resolve(element);
        }
        this.elements = new ArrayList<>();
    }

    void resolve(CodeElement element) {

    }

    public void add(CodeElement element) {
        elements.add(element);
    }
}
