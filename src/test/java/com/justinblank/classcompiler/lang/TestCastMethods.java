package com.justinblank.classcompiler.lang;

import com.justinblank.classcompiler.GenericVars;
import com.justinblank.classcompiler.Method;

import java.util.Collections;

import static com.justinblank.classcompiler.lang.CodeElement.cast;
import static com.justinblank.classcompiler.lang.Literal.literal;
import static com.justinblank.classcompiler.lang.TestMethods.TEST_METHOD;

public class TestCastMethods {

    static Method castIntMethod(Builtin output) {
        var method = new Method(TEST_METHOD, Collections.emptyList(), output.typeString(), new GenericVars());
        method.returnValue(cast(output, literal(1)));
        return method;
    }

    static Method castShortMethod(Builtin output) {
        var method = new Method(TEST_METHOD, Collections.emptyList(), output.typeString(), new GenericVars());
        method.returnValue(cast(output, literal(1)));
        return method;
    }

    static Method castFloatMethod(Builtin output) {
        var method = new Method(TEST_METHOD, Collections.emptyList(), output.typeString(), new GenericVars());
        method.returnValue(cast(output, literal(1.3f)));
        return method;
    }

    static Method castLongMethod(Builtin output) {
        var method = new Method(TEST_METHOD, Collections.emptyList(), output.typeString(), new GenericVars());
        method.returnValue(cast(output, literal(1L + Integer.MAX_VALUE)));
        return method;
    }

    static Method castDoubleMethod(Builtin output) {
        var method = new Method(TEST_METHOD, Collections.emptyList(), output.typeString(), new GenericVars());
        method.returnValue(cast(output, literal(23.4D))); // TODO: dumb value
        return method;
    }
}
