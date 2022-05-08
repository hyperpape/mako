package com.justinblank.classcompiler.lang;

import com.justinblank.classcompiler.ClassBuilder;

public class TestClasses {

    public static ClassBuilder classInPackage(String className, String classPackage) {
        ClassBuilder classBuilder = new ClassBuilder(className, classPackage, Object.class, new String[]{});
        classBuilder.addEmptyConstructor();
        return classBuilder;
    }
}
