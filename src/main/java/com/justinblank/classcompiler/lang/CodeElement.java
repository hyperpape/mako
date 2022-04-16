package com.justinblank.classcompiler.lang;

// Interface representing an expression or statements

import com.justinblank.classcompiler.CompilerUtil;

import java.util.List;

import static com.justinblank.classcompiler.lang.Literal.literal;

public interface CodeElement {

    static Conditional cond(Expression expression) {
        return new Conditional(expression);
    }

    static Expression call(String methodName, Type type, Expression... arguments) {
        return new Call(null, methodName, type, false, false, false, arguments);
    }

    static Expression call(String methodName, Class<?> type, Expression... arguments) {
        return new Call(null, methodName, ReferenceType.of(type), false, false, false, arguments);
    }

    static Expression callInterface(String methodName, Type type, Expression... arguments) {
        return new Call(null, methodName, type, false, false, true, arguments);
    }

    static Expression callInterface(String methodName, Class<?> type, Expression... arguments) {
        return new Call(null, methodName, ReferenceType.of(type), false, false, true, arguments);
    }

    static Expression callStatic(String className, String methodName, Type type, Expression... arguments) {
        return new Call(className, methodName, type, true, false, false, arguments);
    }

    static Expression callStatic(String className, String methodName, Class<?> type, Expression... arguments) {
        return new Call(className, methodName, ReferenceType.of(type), true, false, false, arguments);
    }

    static Expression callStatic(Class<?> cls, String methodName, Type type, Expression... arguments) {
        return new Call(CompilerUtil.internalName(cls), methodName, type, true, false, false, arguments);
    }

    static Expression cast(Type outputType, Expression value) {
        return new Cast(outputType, value);
    }

    static Constructor construct(Type type, Expression...arguments) {
        return new Constructor(type, arguments);
    }

    static Constructor construct(Class<?> type, Expression...arguments) {
        return new Constructor(ReferenceType.of(type), arguments);
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

    static FieldReference get(String fieldName, Class<?> type, Expression expression) {
        return new FieldReference(fieldName, ReferenceType.of(type), expression);
    }

    static StaticFieldReference getStatic(String fieldName, Type classType, Type fieldType) {
        return new StaticFieldReference(fieldName, classType, fieldType);
    }

    static Statement set(String name, Expression value) {
        return new Assignment(name, value);
    }

    static Statement set(String name, Number value) {
        return new Assignment(name, literal(value));
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

    static Statement returnValue(Number number) {
        return new ReturnExpression(literal(number));
    }

    static Expression thisRef() {
        return new ThisRef();
    }

    static Loop loop(Expression condition, List<CodeElement> body) {
        return new Loop(condition, body);
    }
}
