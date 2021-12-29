package com.justinblank.classcompiler.lang;

import java.util.Map;

import static com.justinblank.classcompiler.lang.TypeVariable.fresh;

public class TypeInference {

    private final ReferenceType thisType;

    public TypeInference(String thisType) {
        this.thisType = ReferenceType.of(thisType);
    }

    public Type analyze(CodeElement element, Map<String, TypeVariable> environment) {
        if (element instanceof Call) {
            var call = (Call) element;
            var receiverType = analyze(call.receiver(), environment);
            return null;
        } else if (element instanceof Literal) {
            return TypeVariable.of(Builtin.I);
        } else if (element instanceof Binary) {
            var binary = (Binary) element;
            var leftType = analyze(binary.left, environment);
            var rightType = analyze(binary.right, environment);
            unify(leftType, rightType);
            return binary.operator.type(leftType, rightType);
        } else if (element instanceof Unary) {
            var unary = (Unary) element;
            var expressionType = analyze(unary.expression, environment);
            unify(expressionType, unary.operator.type(expressionType));
            return unary.operator.type(expressionType);
        } else if (element instanceof ThisRef) {
            return TypeVariable.of(thisType);
        } else if (element instanceof VariableRead) {
            return TypeVariable.fresh();
        } else if (element instanceof Assignment) {
            var assignment = (Assignment) element;
            var type = analyze(assignment.expression, environment);
            var existingType = environment.get(assignment.variable);
            if (existingType != null) {
                unify(type, existingType);
            }
            if (type instanceof TypeVariable) {
                environment.put(assignment.variable, (TypeVariable) type);
            }
            else {
                environment.put(assignment.variable, TypeVariable.of(type));
            }
            return type;
        } else if (element instanceof Loop) {
            var loop = (Loop) element;
            analyze(loop.condition, environment);
            for (var loopElt : loop.body) {
                analyze(loopElt, environment);
            }
            // This is ok;
            return null;
        } else if (element instanceof Conditional) {
            var cond = (Conditional) element;
            analyze(cond.condition, environment);
            for (var e : cond.body) {
                analyze(e, environment);
            }
            return null;
        } else if (element instanceof ReturnExpression) {
            var returnExp = (ReturnExpression) element;
            return analyze(returnExp.expression, environment);
        } else if (element instanceof Skip || element instanceof Escape) {
            return null;
        } else {
            throw new IllegalStateException("Unhandled instance: " + element);
        }
    }

    static Type prune(Type t) {
        if (t instanceof TypeVariable) {
            var typeVar = (TypeVariable) t;
            typeVar.type = prune(typeVar.type);
            return typeVar.type;
        }
        return t;
    }

    static void unify(Type t1, Type t2) {
        prune(t1);
        prune(t2);

        if (t1 instanceof TypeVariable) {
            var t1TypeVar = (TypeVariable) t1;
            if (t1TypeVar.type == null) {
                t1TypeVar.type = t2.type();
            }
        }
        else if (t2 instanceof TypeVariable) {
            var t2TypeVar = (TypeVariable) t2;
            if (t2TypeVar.type == null) {
                t2TypeVar.type = t1.type();
            }
        }
        else {
            var unified = t1.type().equals(t2.type());
            if (!unified) {
                throw new TypeCheckException();
            }
        }
    }

    public static class TypeCheckException extends RuntimeException {
    }
}
