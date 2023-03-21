package com.justinblank.classcompiler.lang;

public class Constructor implements Expression {

    public final Type returnType;
    public final Expression[] arguments;

    public Constructor(Type returnType, Expression[] arguments) {
        this.returnType = returnType;
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append("new ").append(returnType.typeString()).append("(");
        for (var i = 0; i < arguments.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(arguments[i]);
        }
        sb.append(")");
        return sb.toString();
    }
}
