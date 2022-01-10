package com.justinblank.classcompiler.lang;

import com.justinblank.classcompiler.Method;

public abstract class Operation implements Expression {

    abstract int asmOP(Method method);
}
