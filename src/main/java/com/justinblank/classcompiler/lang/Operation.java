package com.justinblank.classcompiler.lang;

import com.justinblank.classcompiler.Method;

public abstract class Operation extends Expression {

    abstract int asmOP(Method method);
}
