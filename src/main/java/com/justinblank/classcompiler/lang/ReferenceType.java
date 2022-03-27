package com.justinblank.classcompiler.lang;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class ReferenceType implements Type {

    public Type type() {
        return this;
    }

    public final String typeString;

    ReferenceType(String type) {
        if (StringUtils.isBlank(type)) {
            throw new IllegalArgumentException("Cannot create a reference Type with a blank type string");
        }
        this.typeString = type;
    }

    public static ReferenceType of(Class<?> clz) {
        return of(clz.getCanonicalName());
    }

    public static ReferenceType of(String className) {
        return new ReferenceType(className);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReferenceType that = (ReferenceType) o;
        return Objects.equals(typeString, that.typeString);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeString);
    }

    @Override
    public String toString() {
        return "ReferenceType{" +
                "typeString='" + typeString + '\'' +
                '}';
    }
}
