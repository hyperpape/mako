package com.justinblank.classcompiler;

import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.Opcodes;

import java.util.*;
import java.util.stream.Collectors;

import static org.objectweb.asm.Opcodes.*;

public class ClassBuilder {

    final Map<String, List<Method>> methods = new HashMap<>();
    final List<Field> fields = new ArrayList<>();
    final String superClass;
    final String[] interfaces;
    final List<Block> staticBlocks = new ArrayList<>();
    private final String className;
    private final String classPackage;

    /**
     * Construct a ClassBuilder for a class that inherits from Object and implements no interfaces
     * @param className the classname
     * @param classPackage the package of the class
     */
    public ClassBuilder(String className, String classPackage) {
        this(className, classPackage, "java/lang/Object", null);
    }

    public ClassBuilder(String className, String classPackage, String superClass, String[] interfaces) {
        if (StringUtils.isBlank(className)) {
            throw new IllegalArgumentException("Cannot generate a class with a blank name");
        }
        if (StringUtils.isBlank(superClass)) {
            throw new IllegalArgumentException("Super class name cannot be blank");
        }
        classPackage = CompilerUtil.internalName(StringUtils.defaultString(classPackage));

        this.className = className;
        this.classPackage = classPackage;
        this.superClass = superClass;
        this.interfaces = interfaces;
    }

    public ClassBuilder(String className, String classPackage, Class<?> superClass, String[] interfaces) {
        this(className, classPackage, CompilerUtil.internalName(superClass), interfaces);
    }

    public void addMethod(Method method) {
        this.methods.computeIfAbsent(method.methodName, (s) -> new ArrayList<>()).add(method);
    }

    /**
     * Make an empty method and add it
     *
     * @param methodName the name
     * @param arguments  as descriptors
     * @param returnType as descriptor
     * @return the newly created method
     */
    public Method mkMethod(String methodName, List<String> arguments, String returnType) {
        return mkMethod(methodName, arguments, returnType, null, ACC_PUBLIC);
    }

    /**
     * Make an empty method and add it
     *
     * @param methodName the name
     * @param arguments  as descriptors
     * @param returnType as descriptor
     * @param vars a set of vars for this method
     * @return the newly created method
     */
    public Method mkMethod(String methodName, List<String> arguments, String returnType, Vars vars) {
        return mkMethod(methodName, arguments, returnType, vars, ACC_PUBLIC);
    }

    public Method mkMethod(String methodName, List<String> arguments, String returnType, Vars vars, int modifier) {
        var method = new Method(methodName, arguments, returnType, vars, modifier);
        addMethod(method);
        return method;
    }

    /**
     * Add a stack block to this class. Multiple distinct static blocks will automatically be concatenated in the order
     * that they're added to the class.
     *
     * @return the created block
     */
    public Block addStaticBlock() {
        var b = new Block(staticBlocks.size(), new ArrayList<>());
        staticBlocks.add(b);
        return b;
    }

    public Method addEmptyConstructor() {
        var method = new Method("<init>", List.of(), "V", null);
        var block = method.addBlock();
        block.readThis();
        block.call("<init>", "java/lang/Object", "()V", true);
        block.addReturn(Opcodes.RETURN);
        addMethod(method);
        return method;
    }

    public Block constructorSkeleton(List<String> args, Vars vars) {
        var method = new Method("<init>", args, "V", vars);
        var block = method.addBlock();
        block.readThis();
        block.call("<init>", "java/lang/Object", "()V", true);

        var returnBlock = method.addBlock();
        returnBlock.addReturn(Opcodes.RETURN);
        addMethod(method);
        return block;
    }

    public void construct(Block b, String type) {
        b.construct(type);
        b.operate(DUP);
        b.call("<init>", type, "()V", true);
    }

    protected Collection<Method> allMethods() {
        return methods.values().stream().flatMap(List::stream).collect(Collectors.toSet());
    }

    public boolean hasMethod(String name) {
        return methods.containsKey(name);
    }

    protected List<Field> getFields() {
        return fields;
    }

    public void addField(Field field) {
        fields.add(field);
    }

    public void addConstant(String name, String descriptor, Object value) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(value);
        var field = new Field(ACC_STATIC | ACC_PRIVATE | ACC_FINAL, name, descriptor, null, value);
        fields.add(field);
    }

    public void addArrayConstant(String name, int accessModifier, boolean[] bools) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(bools);
        Block.checkMaxArrayInitializerLength(bools.length);
        var field = new Field(ACC_STATIC | ACC_FINAL | accessModifier, name, "[Z", null, null);
        fields.add(field);
        var block = addStaticBlock();
        Block.pushInitializedArrayToStack(bools, block);
        block.putStatic(name,
                CompilerUtil.internalName(getClassName()),
                "[Z");
    }

    public void addArrayConstant(String name, int accessModifier, int[] ints) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(ints);
        Block.checkMaxArrayInitializerLength(ints.length);
        var field = new Field(ACC_STATIC | ACC_FINAL | accessModifier, name, "[I", null, null);
        fields.add(field);
        var block = addStaticBlock();
        Block.pushInitializedArrayToStack(ints, block);
        block.putStatic(name,
                CompilerUtil.internalName(getClassName()),
                "[I");
    }

    public void addArrayConstant(String name, int accessModifier, long[] longs) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(longs);
        Block.checkMaxArrayInitializerLength(longs.length);
        var field = new Field(ACC_STATIC | ACC_FINAL | accessModifier, name, "[J", null, null);
        fields.add(field);
        var block = addStaticBlock();
        Block.pushInitializedArrayToStack(longs, block);
        block.putStatic(name,
                CompilerUtil.internalName(getClassName()),
                "[J");
    }

    public void addArrayConstant(String name, int accessModifier, float[] floats) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(floats);
        Block.checkMaxArrayInitializerLength(floats.length);
        var field = new Field(ACC_STATIC | ACC_FINAL | accessModifier, name, "[F", null, null);
        fields.add(field);
        var block = addStaticBlock();
        Block.pushInitializedArrayToStack(floats, block);
        block.putStatic(name,
                CompilerUtil.internalName(getClassName()),
                "[F");
    }

    public void addArrayConstant(String name, int accessModifier, double[] doubles) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(doubles);
        Block.checkMaxArrayInitializerLength(doubles.length);
        var field = new Field(ACC_STATIC | ACC_FINAL | accessModifier, name, "[D", null, null);
        fields.add(field);
        var block = addStaticBlock();
        Block.pushInitializedArrayToStack(doubles, block);
        block.putStatic(name,
                CompilerUtil.internalName(getClassName()),
                "[D");
    }


    public void addArrayConstant(String name, int accessModifier, char[] chars) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(chars);
        Block.checkMaxArrayInitializerLength(chars.length);
        var field = new Field(ACC_STATIC | ACC_FINAL | accessModifier, name, "[C", null, null);
        fields.add(field);
        var block = addStaticBlock();
        Block.pushInitializedArrayToStack(chars, block);
        block.putStatic(name,
                CompilerUtil.internalName(getClassName()),
                "[C");
    }


    public void addArrayConstant(String name, int accessModifier, byte[] bytes) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(bytes);
        Block.checkMaxArrayInitializerLength(bytes.length);
        var field = new Field(ACC_STATIC | ACC_FINAL | accessModifier, name, "[B", null, null);
        fields.add(field);
        var block = addStaticBlock();
        Block.pushInitializedArrayToStack(bytes, block);
        block.putStatic(name,
                CompilerUtil.internalName(getClassName()),
                "[B");
    }


    public void addArrayConstant(String name, int accessModifier, short[] shorts) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(shorts);
        Block.checkMaxArrayInitializerLength(shorts.length);
        var field = new Field(ACC_STATIC | ACC_FINAL | accessModifier, name, "[S", null, null);
        fields.add(field);
        var block = addStaticBlock();
        Block.pushInitializedArrayToStack(shorts, block);
        block.putStatic(name,
                CompilerUtil.internalName(getClassName()),
                "[S");
    }


    public String getClassName() {
        return className;
    }

    public String getClassPackage() {
        return classPackage;
    }

    public String getFQCN() {
        if (StringUtils.isBlank(classPackage)) {
            return getClassName();
        }
        return getClassPackage() + "." + getClassName();
    }
}
