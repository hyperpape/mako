package com.justinblank.classcompiler.lang;

import java.util.concurrent.atomic.AtomicLong;

// https://github.com/rob-smallshire/hindley-milner-python
public class TypeVariable implements Type {

    static final AtomicLong COUNTER = new AtomicLong(0);

    final long l;
    Type type = null;

    public Type type() {
        return type;
    }

    private TypeVariable(long l) {
        this.l = l;
    }

    static TypeVariable fresh() {
        return new TypeVariable(COUNTER.incrementAndGet());
    }

    public static TypeVariable of(Type type) {
        var variable = fresh();
        variable.type = type;
        return variable;
    }

    public String typeString() {
        return this.type.typeString();
    }

    @Override
    public boolean resolved() {
        if (type == null) {
            return false;
        }
        return type.resolved();
    }

    // TODO: make useful
    public String toString() {
        // TODO: think harder about this
        if (type == null || type.resolved()) {
            return "TypeVariable: " + type;
        }
        return super.toString();
    }
}
