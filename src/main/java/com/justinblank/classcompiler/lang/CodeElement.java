package com.justinblank.classcompiler.lang;

// Interface representing an expression or statements

import java.util.List;

public interface CodeElement {

    static Expression call(String methodName, Type type, Expression... arguments) {
        return new Call(methodName, type, arguments);
    }

    static Expression read(String variable) {
        return new VariableRead(variable);
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
