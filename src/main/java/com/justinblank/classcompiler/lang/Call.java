package com.justinblank.classcompiler.lang;

import com.justinblank.util.Validate;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

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
        // TODO: this is not necessarily exact enough
        // TODO: should this fail with isSpecial == true?
        if (className == null && !isStatic) {
            this.className = null;
        }
        else {
            this.className = Validate.requireNonEmpty(className, "className cannot be blank");
        }
        this.methodName = Validate.requireNonEmpty(methodName, "methodName cannot be blank");
        this.arguments = Objects.requireNonNull(arguments, "arguments cannot be null");
        this.returnType = Objects.requireNonNull(returnType, "returnType cannot be null");
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(arguments[0]).append('.').append(methodName);
        sb.append('(');
        for (var i = 1; i < arguments.length; i++) {
            if (i > 1) {
                sb.append(",");
            }
            sb.append(arguments[i]);
        }
        sb.append(')');
        return sb.toString();
    }

}
