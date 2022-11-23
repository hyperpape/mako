package com.justinblank.classcompiler.lang;

import java.util.Optional;

import static com.justinblank.classcompiler.lang.Literal.literal;
import static org.objectweb.asm.Opcodes.*;

public enum BinaryOperator {

    AND,
    OR,
    EQUALS,
    NOT_EQUALS,
    GREATER_THAN,
    GREATER_THAN_OR_EQUALS,
    LESS_THAN,
    LESS_THAN_OR_EQUALS,
    PLUS,
    SUBTRACT,
    MULTIPLY,
    DIVIDE,
    MOD;

    public Operation op(Expression left, Expression right) {
        return Binary.of(this, left, right);
    }

    public static Operation plus(Expression left, Expression right) {
        return PLUS.op(left, right);
    }

    public static Operation plus(Number left, Expression right) {
        return PLUS.op(literal(left), right);
    }

    public static Operation plus(Expression left, Number right) {
        return PLUS.op(left, literal(right));
    }

    public static Operation plus(Number left, Number right) {
        return PLUS.op(literal(left), literal(right));
    }

    public static Operation sub(Expression left, Expression right) {
        return SUBTRACT.op(left, right);
    }

    public static Operation sub(Number left, Expression right) {
        return SUBTRACT.op(literal(left), right);
    }

    public static Operation sub(Expression left, Number right) {
        return SUBTRACT.op(left, literal(right));
    }

    public static Operation sub(Number left, Number right) {
        return SUBTRACT.op(literal(left), literal(right));
    }

    public static Operation mul(Expression left, Expression right) {
        return MULTIPLY.op(left, right);
    }

    public static Operation mul(Number left, Expression right) {
        return MULTIPLY.op(literal(left), right);
    }

    public static Operation mul(Expression left, Number right) {
        return MULTIPLY.op(left, literal(right));
    }

    public static Operation mul(Number left, Number right) {
        return MULTIPLY.op(literal(left), literal(right));
    }

    public static Operation div(Number left, Number right) {
        return DIVIDE.op(literal(left), literal(right));
    }

    public static Operation div(Expression left, Expression right) {
        return DIVIDE.op(left, right);
    }

    public static Operation div(Number left, Expression right) {
        return DIVIDE.op(literal(left), right);
    }

    public static Operation div(Expression left, Number right) {
        return DIVIDE.op(left, literal(right));
    }

    public static Operation and(Expression left, Expression right) {
        return AND.op(left, right);
    }

    public static Operation or(Expression left, Expression right) {
        return OR.op(left, right);
    }

    public static Operation eq(Expression left, Expression right) {
        return EQUALS.op(left, right);
    }

    public static Operation eq(Number left, Expression right) {
        return eq(literal(left), right);
    }

    public static Operation eq(Expression left, Number right) {
        return eq(left, literal(right));
    }

    public static Operation eq(Number left, Number right) {
        return eq(literal(left), literal(right));
    }

    public static Operation neq(Expression left, Expression right) {
        return NOT_EQUALS.op(left, right);
    }

    public static Operation neq(Number left, Expression right) {
        return NOT_EQUALS.op(literal(left), right);
    }

    public static Operation neq(Expression left, Number right) {
        return NOT_EQUALS.op(left, literal(right));
    }

    public static Operation neq(Number left, Number right) {
        return NOT_EQUALS.op(literal(left), literal(right));
    }

    public static Operation lt(Expression left, Expression right) {
        return LESS_THAN.op(left, right);
    }

    public static Operation lt(Number left, Expression right) {
        return LESS_THAN.op(literal(left), right);
    }

    public static Operation lt(Expression left, Number right) {
        return LESS_THAN.op(left, literal(right));
    }

    public static Operation lt(Number left, Number right) {
        return LESS_THAN.op(literal(left), literal(right));
    }

    public static Operation lte(Expression left, Expression right) {
        return LESS_THAN_OR_EQUALS.op(left, right);
    }

    public static Operation lte(Number left, Expression right) {
        return LESS_THAN_OR_EQUALS.op(literal(left), right);
    }

    public static Operation lte(Expression left, Number right) {
        return LESS_THAN_OR_EQUALS.op(left, literal(right));
    }

    public static Operation lte(Number left, Number right) {
        return LESS_THAN_OR_EQUALS.op(literal(left), literal(right));
    }

    public static Operation gt(Expression left, Expression right) {
        return GREATER_THAN.op(left, right);
    }

    public static Operation gt(Number left, Expression right) {
        return GREATER_THAN.op(literal(left), right);
    }

    public static Operation gt(Expression left, Number right) {
        return GREATER_THAN.op(left, literal(right));
    }

    public static Operation gt(Number left, Number right) {
        return GREATER_THAN.op(literal(left), literal(right));
    }

    public static Operation gte(Expression left, Expression right) {
        return GREATER_THAN_OR_EQUALS.op(left, right);
    }

    public static Operation gte(Number left, Expression right) {
        return GREATER_THAN_OR_EQUALS.op(literal(left), right);
    }

    public static Operation gte(Expression left, Number right) {
        return GREATER_THAN_OR_EQUALS.op(left, literal(right));
    }

    public static Operation gte(Number left, Number right) {
        return GREATER_THAN_OR_EQUALS.op(literal(left), literal(right));
    }


    public static Operation mod(Number left, Number right) {
        return MOD.op(literal(left), literal(right));
    }

    public static Operation mod(Expression left, Number right) {
        return MOD.op(left, literal(right));
    }

    public static Operation mod(Number left, Expression right) {
        return MOD.op(literal(left), right);
    }

    public static Operation mod(Expression left, Expression right) {
        return MOD.op(left, right);
    }

    public int asmOP(Type left, Type right) {
        switch (this) {
            case PLUS:
                // TODO: do we throw with mis-matched types, or automatically insert casts? 
                if (left.type() instanceof Builtin) {
                    var builtin = (Builtin) left.type();
                    return builtin.addOperation();
                }
                throw new IllegalStateException("");
            case SUBTRACT:
                if (left.type() instanceof Builtin) {
                    var builtin = (Builtin) left.type();
                    return builtin.subtractionOperation();
                }
                return ISUB;
            case MULTIPLY:
                if (left.type() instanceof Builtin) {
                    var builtin = (Builtin) left.type();
                    return builtin.multiplicationOperation();
                }
                return IMUL;
            case DIVIDE:
                if (left.type() instanceof Builtin) {
                    var builtin = (Builtin) left.type();
                    return builtin.divisionOperation();
                }
                return IDIV;
            case MOD:
                if (left.type() instanceof Builtin) {
                    var builtin = (Builtin) left.type();
                    return builtin.modOperation();
                }
                return IDIV;

            case EQUALS:
                if (left.type() instanceof ReferenceType) {
                    return IF_ACMPEQ;
                }
                return IF_ICMPEQ;
            case NOT_EQUALS:
                if (left.type() instanceof ReferenceType) {
                    return IF_ACMPNE;
                }
                return IF_ICMPNE;
            // these are really ugly, and just a result of hacking until I got the results that did the java compiler
            // does
            case LESS_THAN:
                if (left.type() instanceof Builtin && left.type() != Builtin.I) {
                    var builtin = (Builtin) left.type();
                    return builtin.greaterThanOrEqualsOperation();
                }
                return IF_ICMPLT;
            case LESS_THAN_OR_EQUALS:
                if (left.type() instanceof Builtin && left.type() != Builtin.I) {
                    var builtin = (Builtin) left.type();
                    return builtin.greaterThanOperation();
                }
                return IF_ICMPLE;
            case GREATER_THAN:
                if (left.type() instanceof Builtin && left.type() != Builtin.I) {
                    var builtin = (Builtin) left.type();
                    return builtin.lessThanOrEqualsOperation();
                }
                return IF_ICMPGT;
            case GREATER_THAN_OR_EQUALS:
                if (left.type() instanceof Builtin && left.type() != Builtin.I) {
                    var builtin = (Builtin) left.type();
                    return builtin.lessThanOperation();
                }
                return IF_ICMPGE;
            case AND:
                return IAND;
            case OR:
                return IOR;
            default:
                throw new UnsupportedOperationException("");
        }
    }


    public Optional<Integer> comparisonOperation(Type left, Type right) {
        switch (this) {
            case LESS_THAN:
            case LESS_THAN_OR_EQUALS:
                if (left.type() instanceof Builtin) {
                    var builtin = (Builtin) left.type();
                    return Optional.of(builtin.comparisonLesser());
                }
            case GREATER_THAN:
            case GREATER_THAN_OR_EQUALS:
                if (left.type() instanceof Builtin) {
                    var builtin = (Builtin) left.type();
                    return Optional.of(builtin.comparisonGreater());
                }
                throw new IllegalStateException("");
            default:
                return Optional.empty();
        }
    }

    public Type type(Type left, Type right) {
        switch (this) {
            case PLUS:
            case SUBTRACT:
            case MULTIPLY:
            case DIVIDE:
            case MOD:
                return left;
            case EQUALS:
            case NOT_EQUALS:
            case GREATER_THAN:
            case GREATER_THAN_OR_EQUALS:
            case LESS_THAN:
            case LESS_THAN_OR_EQUALS:
            case AND:
            case OR:
                return Builtin.BOOL;
            default:
                throw new IllegalStateException("type operation not implemented for BinaryOperator: " + this);
        }
    }
}