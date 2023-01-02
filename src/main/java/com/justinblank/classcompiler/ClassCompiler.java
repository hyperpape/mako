package com.justinblank.classcompiler;

import com.justinblank.classloader.MyClassLoader;
import org.objectweb.asm.*;
import org.objectweb.asm.util.CheckClassAdapter;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.justinblank.classcompiler.CompilerUtil.pushInt;
import static org.objectweb.asm.Opcodes.*;

// TODO: Simplify/remove functionality that just passes through to ASM
public class ClassCompiler {

    private final ClassWriter classWriter;
    private final ClassVisitor classVisitor;
    private final ClassBuilder classBuilder;
    private final String className;
    private final boolean debug;
    private int lineNumber = 1;

    private final PrintStream printStream;

    public ClassCompiler(ClassBuilder classBuilder) {
        this(classBuilder, false, System.out);
    }

    public ClassCompiler(ClassBuilder classBuilder, boolean debug, PrintStream output) {
        Objects.requireNonNull(classBuilder, "class builder cannot be null");
        Objects.requireNonNull(output, "output cannot be null");
        this.classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        this.debug = debug;
        if (debug) {
            this.classVisitor = new CheckClassAdapter(this.classWriter);
        }
        else {
            this.classVisitor = this.classWriter;
        }
        this.classBuilder = classBuilder;
        this.className = classBuilder.getClassName();
        this.printStream = output;
    }

    public byte[] generateClassAsBytes() {
        return writeClassAsBytes();
    }

    public Class<?> generateClass() {
        byte[] classBytes = generateClassAsBytes();
        var name = CompilerUtil.internalNameToCanonicalName(classBuilder.getFQCN());
        return MyClassLoader.getInstance().loadClass(name, classBytes);
    }

    protected String getClassName() {
        return className;
    }

    public byte[] writeClassAsBytes() {
        byte[] classBytes;
        try {
            for (var method : methodsToWrite(classBuilder.allMethods())) {
                method.setClass(getClassName(), classBuilder.getClassPackage());
                method.resolve();
                if (debug) {
                    printStream.println("Method " + method.methodName + ": " + GraphUtil.methodVis(method));
                }
            }
            if (debug) {
                var stringWriter = new StringWriter();
                var printer = new ClassPrinter(new PrintWriter(stringWriter));
                printer.printClass(classBuilder);
                printStream.println(stringWriter);
            }

            defineClass(classBuilder);
            addFields();

            writeStaticBlocks();
            for (var method : methodsToWrite(classBuilder.allMethods())) {
                writeMethod(method);
            }
            classBytes = classWriter.toByteArray();
        }
        catch (ClassCompilationException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ClassCompilationException("Failed to compile class=" + className, e);
        }
        // TODO: this probably should just be part of the caller's handling?
        if (debug) {
            try (FileOutputStream fos = new FileOutputStream("target/" + className + ".class")) {
                fos.write(classBytes);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return classBytes;
    }

    /**
     * Get the set of methods we actually wish to include in the generated class, ignoring any private method that is not locally called.
     *
     * @param allMethods all methods defined for the class
     * @return the filtered methods
     */
    Set<Method> methodsToWrite(Collection<Method> allMethods) {
        var methods = new HashSet<Method>();
        var methodsToAdd = new Stack<Method>();
        for (var method : allMethods) {
            if (method.modifiers != ACC_PRIVATE) {
                methodsToAdd.add(method);
            }
        }
        while (!methodsToAdd.isEmpty()) {
            var method = methodsToAdd.pop();
            methods.add(method);
            for (var block : method.blocks) {
                for (var op : block.operations) {
                    if (op.inst == Operation.Inst.CALL) {
                        var spec = op.spec;
                        if (spec.isSelf || spec.className.equals(getClassName())) {
                            for (var otherMethod : allMethods) {
                                if (otherMethod.methodName.equals(spec.name) && !methods.contains(otherMethod)) {
                                    methodsToAdd.add(otherMethod);
                                }
                            }
                        }
                    }
                }
            }
        }
        return methods;
    }

    protected ClassBuilder getClassBuilder() {
        return classBuilder;
    }

    private void writeStaticBlocks() {
        if (classBuilder.staticBlocks.isEmpty()) {
            return;
        }
        var mv = classVisitor.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
        mv.visitCode();

        for (var b : classBuilder.staticBlocks) {
            visitBlock(mv, Optional.empty(), b);
        }
        mv.visitInsn(RETURN);
        if (debug) {
            mv.visitMaxs(6, 6);
        }
        else {
            mv.visitMaxs(-1, -1);
        }
        mv.visitEnd();
    }

    private void defineClass(ClassBuilder builder) {
        classVisitor.visit(Opcodes.V1_8, ACC_PUBLIC, CompilerUtil.internalName(builder.getFQCN()), null, classBuilder.superClass, classBuilder.interfaces);
    }

    protected final void addFields() {
        for (var field : classBuilder.fields) {
            classVisitor.visitField(field.modifier, field.name, field.descriptor, field.signature, field.value);
        }
    }

    void writeMethod(Method method) {
        try {
            var mv = classVisitor.visitMethod(method.modifiers, method.methodName, method.descriptor(), null, null);
            var vars = method.getMatchingVars();

            mv.visitCode();
            for (var block : method.blocks) {
                visitBlock(mv, vars, block);
            }
            if (debug) {
                mv.visitMaxs(12, 12);
            }
            else {
                mv.visitMaxs(-1, -1);
            }
            mv.visitEnd();
        }
        catch (Exception e) {
            System.out.println("Error in Method=" + method.methodName);
            throw e;
        }
    }

    private void visitBlock(MethodVisitor mv, Optional<Vars> vars, Block block) {
        mv.visitLabel(block.getLabel());
        for (var op : block.operations) {
            writeOperation(mv, vars, op);
        }
    }

    private void writeOperation(MethodVisitor mv, Optional<Vars> vars, Operation op) {
        switch (op.inst) {
            case INCREMENT:
                mv.visitIincInsn(vars.get().indexByName(op.spec.name), op.count);
                return;
            case RETURN:
            case PASSTHROUGH:
                mv.visitInsn(op.count);
                return;
            case INVOKESPECIAL:
            case INVOKESTATIC:
            case INVOKEINTERFACE:
            case CALL:
                var spec = op.spec;
                var name = spec.isSelf ? this.className : spec.className;
                int opcode;
                var isInterface = false;
                if (op.inst == Operation.Inst.INVOKESTATIC) {
                    opcode = INVOKESTATIC;
                }
                else if (op.inst == Operation.Inst.INVOKESPECIAL) {
                    opcode = INVOKESPECIAL;
                }
                else if (op.inst == Operation.Inst.INVOKEINTERFACE) {
                    opcode = INVOKEINTERFACE;
                    isInterface = true;
                }
                else {
                    opcode = INVOKEVIRTUAL;
                }
                mv.visitMethodInsn(opcode, name, spec.name, spec.descriptor, isInterface);
                return;
            case VALUE:
                if (op.number == null) {
                    pushInt(mv, op.count);
                }
                else {
                    mv.visitLdcInsn(op.number);
                }
                return;
            case READ_VAR:
                handleReadVar(mv, op, vars);
                return;
            case READ_FIELD:
                spec = op.spec;
                name = spec.isSelf ? this.className : spec.className;
                mv.visitFieldInsn(GETFIELD, name, op.spec.name, op.spec.descriptor);
                return;
            case READ_STATIC:
                spec = op.spec;
                name = spec.isSelf ? this.className : spec.className;
                mv.visitFieldInsn(GETSTATIC, name, op.spec.name, op.spec.descriptor);
                return;
            case SET_VAR:
                if (op.spec != null) {
                    switch (op.spec.descriptor) {
                        case "I":
                        case "Z":
                        case "D":
                        case "F":
                        case "C":
                            mv.visitVarInsn(ISTORE, op.count);
                            break;
                        default:
                            mv.visitVarInsn(ASTORE, op.count);
                    }
                }
                else {
                    mv.visitVarInsn(ISTORE, op.count);
                }
                return;
            case SET_FIELD:
                mv.visitFieldInsn(PUTFIELD, op.spec.className, op.spec.name, op.spec.descriptor);
                return;
            case PUT_STATIC:
                spec = op.spec;
                name = spec.isSelf ? this.className : spec.className;
                mv.visitFieldInsn(PUTSTATIC, name, op.spec.name, op.spec.descriptor);
                return;
            case JUMP:
                mv.visitJumpInsn(op.count, op.target.getLabel());
                return;
            case NEW:
                mv.visitTypeInsn(NEW, op.spec.descriptor);
                return;
            case NEWARRAY:
                if (op.spec == null) {
                    mv.visitIntInsn(NEWARRAY, op.count);
                }
                else {
                    mv.visitTypeInsn(ANEWARRAY, op.spec.descriptor);
                }
                return;
            case TABLESWITCH:
                var blocks = op.blockTargets;
                var labels = blocks.stream().map(Block::getLabel).collect(Collectors.toList());
                mv.visitTableSwitchInsn(op.count, op.count + blocks.size() - 1, op.target.getLabel(), labels.toArray(new Label[0]));
                return;
            case LOOKUPSWITCH:
                blocks = op.blockTargets;
                labels = blocks.stream().map(Block::getLabel).collect(Collectors.toList());
                var keys = new int[labels.size()];
                for (var i = 0; i < op.ints.size(); i++) {
                    keys[i] = op.ints.get(i);
                }
                mv.visitLookupSwitchInsn(op.blockTargets.get(0).getLabel(), keys, labels.toArray(new Label[0]));
                return;
            default:
                throw new IllegalStateException("Unrecognized opcode: " + op.inst);
            }
        }

    private void handleReadVar(MethodVisitor mv, Operation op, Optional<Vars> vars) {
        switch (op.spec.descriptor) {
            case "C":
            case "I":
            case "B":
            case "Z":
                if (op.count < 0) {
                    throw new IllegalArgumentException("Illegal variable: index=" + op.count + ", Spec=" + op.spec);
                }
                mv.visitVarInsn(ILOAD, op.count);
                return;
            case "L":
                if (op.count < 0) {
                    throw new IllegalArgumentException("Illegal variable: index=" + op.count + ", Spec=" + op.spec);
                }
                mv.visitVarInsn(LLOAD, op.count);
                return;
            case "F":
                if (op.count < 0) {
                    throw new IllegalArgumentException("Illegal variable: index=" + op.count + ", Spec=" + op.spec);
                }
                mv.visitVarInsn(FLOAD, op.count);
                return;
            case "D":
                if (op.count < 0) {
                    throw new IllegalArgumentException("Illegal variable: index=" + op.count + ", Spec=" + op.spec);
                }
                mv.visitVarInsn(DLOAD, op.count);
                return;
            default:
                AtomicInteger count = new AtomicInteger(op.count);
                vars.ifPresent((v) -> {
                    if (op.spec.name != null) {
                        count.set(v.indexByName(op.spec.name));
                    }
                });
                if (op.count < 0) {
                    throw new IllegalArgumentException("Illegal variable: index=" + op.count + ", Spec=" + op.spec);
                }
                mv.visitVarInsn(ALOAD, count.get());
                return;
        }

    }

    protected int newLine() {
        return ++lineNumber;
    }

    protected int currentLine() {
        return lineNumber;
    }

    protected void visitLine(MethodVisitor mv) {
        var label = new Label();
        mv.visitLabel(label);
        mv.visitLineNumber(newLine(), label);
    }
}
