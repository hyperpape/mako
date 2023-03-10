package com.justinblank.classcompiler.lang;

import com.justinblank.classcompiler.ClassCompilationException;
import com.justinblank.classcompiler.CompilerUtil;
import com.justinblank.classcompiler.Method;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

public class TypeInference {

    private final ReferenceType thisType;

    private final Method method;

    public TypeInference(String thisType, Method method) {
        this.thisType = ReferenceType.of(thisType);
        this.method = method;
    }

    public void analyze(Collection<CodeElement> elements, Map<String, TypeVariable> environment) {
        for (var e : elements) {
            analyze(e, environment);
        }
    }

    public Type analyze(CodeElement element, Map<String, TypeVariable> environment) {
        Objects.requireNonNull(element, "Cannot perform type inference on a null element");
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
            return Void.VOID;
        } else if (element instanceof Loop) {
            var loop = (Loop) element;
            if (loop.condition != null) {
                analyze(loop.condition, environment);
            }
            analyze(loop.body, environment);
            return Void.VOID;
        } else if (element instanceof Switch) {
            var s = (Switch) element;
            for (var c : new HashSet<>(s.cases.values())) {
                analyze(c, environment);
            }
            analyze(s.defaultCase, environment);
            return Void.VOID;
        } else if (element instanceof Conditional) {
            var cond = (Conditional) element;
            analyze(cond.condition, environment);
            analyze(cond.body, environment);
            return Void.VOID;
        } else if (element instanceof ReturnExpression) {
            var returnExp = (ReturnExpression) element;
            var returnValue =  analyze(returnExp.expression, environment);
            Type returnType = Type.fromDescriptor(method.returnType);
            unify(returnValue, returnType);
            return returnValue;
        } else if (element instanceof ReturnVoid) {
            return Void.VOID;
        } else if (element instanceof Skip || element instanceof Escape) {
            return Void.VOID;
        } else if (element instanceof NewArray) {
            return ArrayType.of(((NewArray) element).type);
        } else if (element instanceof ArrayRead) {
            var arrayType = analyze(((ArrayRead) element).arrayRef, environment);
            return ((ArrayType) arrayType.type()).elementType;
        } else if (element instanceof ArraySet) {
            return Void.VOID;
        } else if (element instanceof FieldReference) {
            return ((FieldReference) element).type;
        } else if (element instanceof StaticFieldReference) {
            return ((StaticFieldReference) element).type;
        } else if (element instanceof ArrayLength) {
            return Builtin.I;
        } else if (element instanceof Cast) {
            return ((Cast) element).outputType;
        } else if (element instanceof NoOpStatement) {
            // Not obvious if this is right...
            return Void.VOID;
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
            else {
                // YOLO infinite loop?
                unify(t1TypeVar.type, t2);
            }
        }
        else if (t2 instanceof TypeVariable && !t2.resolved()) {
            var t2TypeVar = (TypeVariable) t2;
            if (t2TypeVar.type == null) {
                t2TypeVar.type = t1.type();
            }
        }
        else {
            var t1Type = t1.type();
            var t2Type = t2.type();
            if (typesAreIncompatible(t1Type, t2Type)) {
                throw new TypeCheckException("Cannot unify " + t1Type.typeString() + " with " + t2Type.typeString());
            }
        }
    }

    /**
     * Determine if two concrete types are definitely incompatible
     * @param t1Type
     * @param t2Type
     * @return true if the two types are definitely incompatible
     */
    private static boolean typesAreIncompatible(Type t1Type, Type t2Type) {
        if (t1Type instanceof TypeVariable) {
            if (t2Type instanceof TypeVariable) {
                if (t1Type.resolved() && t2Type.resolved()) {
                    return typesAreIncompatible(t1Type.type(), t2Type.type());
                }
                return false;
            }
            else {
                if (t1Type.resolved()) {
                    return typesAreIncompatible(t1Type.type(), t2Type);
                }
            }
            return false;
        }
        else if (t2Type instanceof TypeVariable) {
            if (!t2Type.resolved()) {
                return false;
            }
        }
        if (t1Type.getClass() != t2Type.getClass()) {
            return true;
        }
        return false;
    }

    public static class TypeCheckException extends ClassCompilationException {

        public TypeCheckException(String s) {
            super(s);
        }
    }
}
