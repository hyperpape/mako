package com.justinblank.classcompiler.lang;

// Interface representing an expression or statements

import com.justinblank.classcompiler.CompilerUtil;

import java.util.List;

public interface CodeElement {

    static Conditional cond(Expression expression) {
        return new Conditional(expression);
    }

    static Expression call(String methodName, Type type, Expression... arguments) {
        return new Call(null, methodName, type, false, arguments);
    }

    static Expression callStatic(String className, String methodName, Type type, Expression... arguments) {
        return new Call(className, methodName, type, true, arguments);
    }

    static Expression callStatic(Class<?> cls, String methodName, Type type, Expression... arguments) {
        return new Call(CompilerUtil.internalName(cls), methodName, type, true, arguments);
    }

    static ArrayLength arrayLength(Expression expression) {
        return new ArrayLength(expression);
    }

    static Expression read(String variable) {
        return new VariableRead(variable);
    }

    static FieldReference get(String fieldName, Type type, Expression expression) {
        return new FieldReference(fieldName, type, expression);
    }

    static Statement set(String name, Expression value) {
        return new Assignment(name, value);
    }

    static Escape escape() {
        return new Escape();
    }

    static Skip skip() {
        return new Skip();
    }

    static Statement returnValue(Expression expression) {
        return new ReturnExpression(expression);
    }

    static Expression thisRef() {
        return new ThisRef();
    }

    static Loop loop(Expression condition, List<CodeElement> body) {
        return new Loop(condition, body);
    }
}
