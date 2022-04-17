package com.justinblank.classcompiler.lang;

public class Call implements Expression {

    public final String methodName;
    public final Expression[] arguments;
    public final Type returnType;
    public final boolean isStatic;
    public final boolean isSpecial;
    public final boolean isInterface;
    public final String className;

    Call(String className, String methodName, Type returnType, boolean isStatic, boolean isSpecial, boolean isInterface, Expression... arguments) {
        if (!isStatic && arguments.length == 0) {
            throw new IllegalArgumentException("Cannot call an instance method without a receiver");
        }
        this.className = className;
        this.methodName = methodName;
        this.arguments = arguments;
        this.returnType = returnType;
        this.isStatic = isStatic;
        this.isSpecial = isSpecial;
        this.isInterface = isInterface;
    }

    /**
     * @return The expression that this method is invoked upon, assuming it is not a static call.
     */
    public Expression receiver() {
        return this.arguments[0];
    }
}
