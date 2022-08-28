package com.justinblank.classcompiler;

public class GraphUtil {

    public static String methodVis(Method method) {
        StringBuilder sb = new StringBuilder();

        sb.append("digraph G {");
        for (var b : method.blocks) {
            for (var o : b.operations) {
                if (o.inst == Operation.Inst.JUMP) {
                    sb.append("\n");
                    sb.append(b.number).append(" -> ").append(o.target.number);
                    sb.append("[ label = ").append(ClassPrinter.getRepresentation(o)).append(" ]").append(";");
                }
            }
            if (b.number < method.blocks.size() - 1) {
                sb.append("\n");
                sb.append(b.number).append(" -> ").append(b.number + 1);
            }
        }

        sb.append("}");
        return sb.toString();
    }
}
