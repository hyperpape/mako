package com.justinblank.classcompiler.aocexamples;

import com.justinblank.classcompiler.*;
import com.justinblank.classcompiler.lang.ArrayType;
import com.justinblank.classcompiler.lang.Builtin;
import com.justinblank.classcompiler.lang.CodeElement;
import com.justinblank.classcompiler.lang.ReferenceType;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Supplier;

import static com.justinblank.classcompiler.lang.ArrayRead.arrayRead;
import static com.justinblank.classcompiler.lang.BinaryOperator.*;
import static com.justinblank.classcompiler.lang.CodeElement.*;
import static com.justinblank.classcompiler.lang.Literal.literal;
import static junit.framework.TestCase.fail;
import static org.objectweb.asm.Opcodes.*;

public class Problem7 {

    @Test
    public void problem7() {
        try {
            var builder = new ClassBuilder("Problem7", "java/lang/Object", new String[]{CompilerUtil.internalName(Supplier.class)});
            builder.addMethod(builder.addEmptyConstructor());
            builder.addMethod(mkSolveMethod());
            builder.addMethod(mkGetMethod());

            var cls = new ClassCompiler(builder);
            Class<?> compiled = cls.generateClass();
            var instance = (Supplier<Integer>) compiled.getDeclaredConstructors()[0].newInstance();
            System.out.println(instance.get());
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

    }

    // TODO: why do I have a separate solve method?
    private Method mkSolveMethod() {
        var vars = new GenericVars("crabPositions", "min", "index", "guess", "total", "difference");
        var method = new Method("solve", List.of(), CompilerUtil.descriptor(Integer.class), vars);
        method.set("crabPositions", callStatic("com/justinblank/classcompiler/aocexamples/Problem7",
                "readInput", ArrayType.of(Builtin.I)));
        method.set("min", Integer.MAX_VALUE);
        method.set("guess", 0);

        method.loop(null,
                List.of(
                    set("index", 0),
                    set("total", 0),
                    loop(lt(read("index"), arrayLength(read("crabPositions"))),
                        List.of(
                                set("difference", callStatic(Math.class, "abs", Builtin.I,
                                    sub(read("guess"), arrayRead(read("crabPositions"), read("index"))))),
                                set("total", plus(read("total"), read("difference"))),
                                set("index", plus(read("index"), 1))
                    )),
                    set("guess", plus(read("guess"), literal(1))),
                    cond(lt(read("total"), read("min"))).withBody(List.of(
                        set("min", read("total"))
                    )),
                    cond(gt(read("total"), read("min"))).withBody(List.of(
                            returnValue(callStatic(CompilerUtil.internalName(Integer.class), "valueOf",
                                    ReferenceType.of(Integer.class), read("min"))))
                    ))
                );
        method.returnValue(callStatic(CompilerUtil.internalName(Integer.class), "valueOf", ReferenceType.of(Integer.class), read("min")));
        return method;
    }

    private Method mkGetMethod() {
        var vars = new GenericVars("crabPositions", "min", "index", "guess", "total", "difference");
        var method = new Method("get", List.of(), CompilerUtil.descriptor(Object.class), vars, ACC_PUBLIC | ACC_BRIDGE| ACC_SYNTHETIC);
        method.returnValue(CodeElement.call("solve", ReferenceType.of(Integer.class), thisRef()));
        return method;
    }

    public static int[] readInput() throws Exception {
        var url = Problem7.class.getClassLoader().getResource("aoc7.txt");
        var lines = Files.readAllLines(Path.of(url.toURI()));
        String[] input = lines.get(0).split(",");
        int[] ints = new int[input.length];
        for (var i = 0; i < input.length; i++) {
            ints[i] = Integer.parseInt(input[i]);
        }
        return ints;
    }
}
