package com.justinblank.classcompiler.lang;

import java.util.Map;

public class TypeInference {

    private final ReferenceType thisType;

    public TypeInference(String thisType) {
        this.thisType = ReferenceType.of(thisType);
    }

    public Type analyze(CodeElement element, Map<String, TypeVariable> environment) {
        if (element instanceof Call) {
            var call = (Call) element;
            return call.returnType;
        } else if (element instanceof Constructor) {
            var constructor = (Constructor) element;
            for (var arg : constructor.arguments) {
                analyze(arg, environment);
            }
            return constructor.returnType;
        } else if (element instanceof Literal) {
            var lit = (Literal) element;
            if (lit.value instanceof Integer) {
                return TypeVariable.of(Builtin.I);
            }
            else if (lit.value instanceof Float) {
                return TypeVariable.of(Builtin.F);
            }
            else if (lit.value instanceof Long) {
                return TypeVariable.of(Builtin.L);
            }
            else if (lit.value instanceof Double) {
                return TypeVariable.of(Builtin.D);
            }
            else {
                throw new IllegalStateException("Unrecognized class for literal value:" + lit.value.getClass());
            }
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
            return environment.computeIfAbsent(((VariableRead) element).variable, (k) -> TypeVariable.fresh());
        } else if (element instanceof Assignment) {
            var assignment = (Assignment) element;
            var type = analyze(assignment.expression, environment);
            var existingType = environment.get(assignment.variable);
            if (existingType != null) {
                unify(type, existingType);
            }
            if (type instanceof TypeVariable) {
                environment.put(assignment.variable, (TypeVariable) type);
            } else {
                environment.put(assignment.variable, TypeVariable.of(type));
            }
            return type;
        } else if (element instanceof FieldSet) {
            return null; // YOLO // TODO
        } else if (element instanceof Loop) {
            var loop = (Loop) element;
            if (loop.condition != null) {
                analyze(loop.condition, environment);
            }
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
        } else if (element instanceof ReturnVoid) {
            return Void.VOID;
        } else if (element instanceof Skip || element instanceof Escape) {
            return null;
        } else if (element instanceof NewArray) {
            return ArrayType.of(((NewArray) element).type);
        } else if (element instanceof ArrayRead) {
            var arrayType = analyze(((ArrayRead) element).arrayRef, environment);
            return ((ArrayType) arrayType.type()).elementType;
        } else if (element instanceof ArraySet) {
            return null; // TODO is this right?
        } else if (element instanceof FieldReference) {
            return ((FieldReference) element).type;
        } else if (element instanceof StaticFieldReference) {
            return ((StaticFieldReference) element).type;
        } else if (element instanceof ArrayLength) {
            return Builtin.I;
        } else if (element instanceof Cast) {
            return ((Cast) element).outputType;
        } else {
            throw new IllegalStateException("Unhandled instance of CodeElement during type inference: " + element);
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
        else if (t2 instanceof TypeVariable && !t2.resolved()) {
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
