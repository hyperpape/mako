package com.justinblank.classcompiler;

import static com.justinblank.classcompiler.Operation.Inst.JUMP;
import static net.bytebuddy.jar.asm.Opcodes.GOTO;

public class GraphUtil {

    public static String methodVis(Method method) {
        StringBuilder sb = new StringBuilder();

        sb.append("digraph G {");
        for (var b : method.blocks) {
            if (b.isEmpty() && b.number < method.blocks.size() - 1) {
                continue;
            }
            for (var o : b.operations) {
                if (o.inst == JUMP) {
                    sb.append("\n");
                    sb.append(b.number).append(" -> ").append(o.target.number);
                    sb.append("[ label = ").append(ClassPrinter.getRepresentation(o)).append(" ]").append(";");
                }
            }
            if (b.number < method.blocks.size() - 1) {
                var op = b.operations.get(b.operations.size() - 1);
                if (op.inst == JUMP && op.count == GOTO) {
                    continue;
                }
                sb.append("\n");
                sb.append(b.number).append(" -> ").append(b.number);
            }
        }

        sb.append("}");
        return sb.toString();
    }

    public static Block actualTarget(Method method, int number) {
        var target = method.blocks.get(number);
        while (target.number < method.blocks.size() - 1) {
            if (target.isEmpty()) {
                target = method.blocks.get(target.number + 1);
            }
            else {
                return target;
            }
        }
        return target;
    }
}
