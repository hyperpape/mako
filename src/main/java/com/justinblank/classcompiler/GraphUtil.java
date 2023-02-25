package com.justinblank.classcompiler;

import java.util.List;

import static com.justinblank.classcompiler.Operation.Inst.JUMP;
import static org.objectweb.asm.Opcodes.GOTO;

public class GraphUtil {

    public static String methodVis(Method method) {
        StringBuilder sb = new StringBuilder();

        sb.append("digraph G {");
        List<Block> blocks = method.getBlocks();
        for (var b : blocks) {
            if (b.isEmpty() && b.number < blocks.size() - 1) {
                continue;
            }
            for (var o : b.operations) {
                if (o.inst == JUMP) {
                    sb.append("\n");
                    sb.append(b.number).append(" -> ").append(o.target.number);
                    sb.append("[ label = ").append(ClassPrinter.getRepresentation(o)).append(" ]").append(";");
                }
            }
            if (b.number < blocks.size() - 1) {
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
        List<Block> blocks = method.getBlocks();
        var target = blocks.get(number);
        while (target.number < blocks.size() - 1) {
            if (target.isEmpty()) {
                target = blocks.get(target.number + 1);
            }
            else {
                return target;
            }
        }
        return target;
    }
}
