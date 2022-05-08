package com.justinblank.classcompiler.lang;

import com.justinblank.classcompiler.ClassBuilder;
import com.justinblank.classcompiler.CompilerUtil;

import java.util.ArrayList;

import static com.justinblank.classcompiler.lang.CodeElement.*;
import static com.justinblank.classcompiler.lang.Literal.literal;

public class TestClasses {

    public static ClassBuilder classInPackage(String className, String classPackage) {
        ClassBuilder classBuilder = new ClassBuilder(className, classPackage, Object.class, new String[]{});
        classBuilder.addEmptyConstructor();
        var method = classBuilder.mkMethod("createAndReturnInteger", new ArrayList<>(), CompilerUtil.descriptor(Integer.class));
        method.returnValue(callStatic(Integer.class, "valueOf", ReferenceType.of(Integer.class), literal(0)));

        var method2 = classBuilder.mkMethod("callOwnMethod", new ArrayList<>(), CompilerUtil.descriptor(Integer.class));
        method2.returnValue(call("createAndReturnInteger", ReferenceType.of(Integer.class), thisRef()));
        return classBuilder;
    }
}
