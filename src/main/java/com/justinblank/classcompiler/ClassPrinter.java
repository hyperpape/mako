package com.justinblank.classcompiler;

import org.objectweb.asm.Opcodes;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.justinblank.classcompiler.Operation.Inst.CALL;

public class ClassPrinter {

    static final Map<Integer, String> REPRESENTATIONS = new HashMap<Integer, String>();

    static {
        // TODO: fix gaps
        REPRESENTATIONS.put(Opcodes.POP, "POP");
        REPRESENTATIONS.put(Opcodes.SWAP, "SWAP");
        REPRESENTATIONS.put(Opcodes.IADD, "IADD");
        REPRESENTATIONS.put(Opcodes.IMUL, "IMUL");
        REPRESENTATIONS.put(Opcodes.IAND, "IAND");
        REPRESENTATIONS.put(Opcodes.IOR, "IOR");
        REPRESENTATIONS.put(Opcodes.IXOR, "IXOR");
        REPRESENTATIONS.put(Opcodes.IFEQ, "IFEQ");
        REPRESENTATIONS.put(Opcodes.IFNE, "IFNE");
        REPRESENTATIONS.put(Opcodes.IFLT, "IFLT");
        REPRESENTATIONS.put(Opcodes.IFGE, "IFGE");
        REPRESENTATIONS.put(Opcodes.IFGT, "IFGT");
        REPRESENTATIONS.put(Opcodes.IFLE, "IFLE");
        REPRESENTATIONS.put(Opcodes.IF_ICMPEQ, "IF_ICMPEQ");
        REPRESENTATIONS.put(Opcodes.IF_ICMPNE, "ICMPNE");
        REPRESENTATIONS.put(Opcodes.IF_ICMPLT, "IF_ICMPLT");
        REPRESENTATIONS.put(Opcodes.IF_ICMPGE, "IF_ICMPGE");
        REPRESENTATIONS.put(Opcodes.IF_ICMPGT, "IF_ICMPGT");
        REPRESENTATIONS.put(Opcodes.IF_ICMPLE, "IF_ICMPLE");
        REPRESENTATIONS.put(Opcodes.IF_ACMPEQ, "IF_ACMPEQ");
        REPRESENTATIONS.put(Opcodes.IF_ACMPNE, "IF_ACMPNE");
        REPRESENTATIONS.put(Opcodes.IFNONNULL, "IFNONNULL");
        REPRESENTATIONS.put(Opcodes.IFNULL, "IFNULL");
        REPRESENTATIONS.put(Opcodes.GOTO, "GOTO");
    }

    int indentation;
    PrintWriter pw;
    boolean indented = false;

    protected ClassPrinter(PrintWriter pw) {
        this.pw = pw;
    }

    protected void print(Object obj) {
        if (!indented) {
            indent();
            indented = true;
        }
        pw.print(obj);
    }

    private void indent() {
        for (int i = 0; i < indentation; i++) {
            pw.print("    ");
        }
    }

    protected void println(Object obj) {
        if (!indented) {
            indent();
        }
        pw.println(obj);
        indented = false;
    }

    protected void printOperation(Operation op, Optional<Vars> vars) {
        print(op.inst);
        print(' ');
        switch (op.inst) {
            case PASSTHROUGH:
                String rep = getRepresentation(op);
                if (rep != null) {
                    println(rep);
                }
                else {
                    println(op.count);
                }
                break;
            case VALUE:
                if (op.number == null) {
                    println(op.count);
                }
                else {
                    println(op.number);
                }
                return;
            case JUMP:
                print(REPRESENTATIONS.get(op.count));
                print(' ');
            case CHECK_BOUNDS:
                println(op.target);
                return;
            case CALL:
            case READ_STATIC:
                print(op.spec.isSelf ? "Self" : op.spec.className);
                print(op.inst == CALL ? "#" : ".");
                println(op.spec.name);
                return;
            case READ_FIELD:
                if (op.spec != null) {
                    if (op.spec.isSelf) {
                        print("this.");
                    }
                    println(op.spec.name);
                }
                return;
            case READ_VAR:
            case SET_VAR:
                if (op.spec != null) {
                    if (op.spec.isSelf) {
                        println("this");
                    } else if (op.spec.name != null) {
                        println(op.spec.name);
                    } else {
                        println(vars.map(v -> v.nameByIndex(op.count)).orElse(""));
                    }
                } else {
                    println(op.count);
                }
                return;
            default:
                println("");
        }
    }

    public static String getRepresentation(Operation op) {
        return REPRESENTATIONS.get(op.count);
    }

    void printBlock(Block block, Optional<Vars> vars) {
        println("BLOCK" + block.number + ":");
        indentation++;
        for (Operation op : block.operations) {
            printOperation(op, vars);
        }
        indentation--;
    }

    void printMethod(Method method) {
        println("METHOD " + method.methodName.toUpperCase() + ":");
        for (Block block : method.getBlocks()) {
            indentation++;
            printBlock(block, method.getMatchingVars());
            indentation--;
        }
    }

    void printClass(ClassBuilder builder) {
        for (Method method : builder.allMethods()) {
            indentation++;
            printMethod(method);
            indentation--;
        }
    }

    static String asString(ClassBuilder builder) {
        var stringWriter = new StringWriter();
        var printer = new ClassPrinter(new PrintWriter(stringWriter));
        printer.printClass(builder);
        return stringWriter.toString();
    }

    static String asString(Method method) {
        var stringWriter = new StringWriter();
        var printer = new ClassPrinter(new PrintWriter(stringWriter));
        printer.printMethod(method);
        return stringWriter.toString();
    }
}
