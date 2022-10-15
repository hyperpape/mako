package com.justinblank.classcompiler.lang;

import java.util.*;

public class Switch extends Statement {

    Expression expression;
    Map<Object, List<CodeElement>> cases = new HashMap<>();
    List<CodeElement> defaultCase = null;

    Boolean isIntegerSwitch = null;

    public Switch(Expression expression) {
        Objects.requireNonNull(expression, "Cannot have a null expression for a switch constructor");
        this.expression = expression;
    }

    public boolean isComplete() {
        return defaultCase != null && !cases.isEmpty();
    }

    public Boolean getIntegerSwitch() {
        return isIntegerSwitch;
    }

    public List<Integer> intCases() {
        List<Integer> caseKeys = new ArrayList<>();
        for (var k : cases.keySet()) {
            caseKeys.add((Integer) k);
        }
        Collections.sort(caseKeys);
        return caseKeys;
    }

    public static boolean isDense(List<Integer> integers) {
        int min = integers.get(0);
        int max = integers.get(integers.size() - 1);
        if (max - min != (integers.size() - 1)) {
            return false;
        }
        for (var i = 0; i < integers.size() - 1; i++) {
            if (integers.get(i+1) != integers.get(i) + 1) {
                return false;
            }
        }
        return true;
    }

    public List<CodeElement> getElements(Object o) {
        return cases.get(o);
    }

    public Switch setCase(Integer value, CodeElement body) {
        return setCase(value, List.of(body));
    }

    public Switch setCase(Integer value, List<CodeElement> body) {
        if (isIntegerSwitch == null) {
            isIntegerSwitch = true;
        }
        else if (!isIntegerSwitch) {
            throw new IllegalArgumentException("Tried to add an integer to string switch statement");
        }
        if (cases.containsKey(value)) {
            throw new IllegalStateException("Tried to redefine switch case=" + value);
        }
        cases.put(value, body);
        return this;
    }

    public Switch setCase(String value, CodeElement body) {
        return setCase(value, List.of(body));
    }

    public Switch setCase(String value, List<CodeElement> body) {
        throw new UnsupportedOperationException("TODO");
//        if (isIntegerSwitch == null) {
//            isIntegerSwitch = false;
//        }
//        else if (isIntegerSwitch) {
//            throw new IllegalArgumentException("Tried to add a string to an integer switch statement");
//        }
//        if (cases.containsKey(value)) {
//            throw new IllegalStateException("Tried to redefine switch case='" + value + "'");
//        }
//        cases.put(value, body);
    }

    public List<CodeElement> getDefaultCase() {
        return Collections.unmodifiableList(defaultCase);
    }


    public void setDefault(CodeElement element) {
        this.defaultCase = List.of(element);
    }

    public void setDefault(List<CodeElement> elements) {
        this.defaultCase = elements;
    }

    public CodeElement getExpression() {
        return expression;
    }
}