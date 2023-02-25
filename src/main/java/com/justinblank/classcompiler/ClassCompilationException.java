package com.justinblank.classcompiler;

import org.apache.commons.lang3.StringUtils;

public class ClassCompilationException extends RuntimeException {

    // Non-null
    private String className;
    // Nullable
    private String methodName;

    void setClassName(String className) {
        if (this.className == null) {
            this.className = className;
        }
    }

    void setMethodName(String methodName) {
        if (this.methodName == null) {
            this.methodName = methodName;
        }
    }

    @Override
    public String getMessage() {
        var message = super.getMessage();
        if (StringUtils.isBlank(message)) {
            return "ClassName=" + className + ", MethodName=" + methodName;
        }
        else {
            return message + ", ClassName=" + className + ", MethodName=" + methodName;
        }
    }

    public ClassCompilationException(String message) {
        super(message);
    }

    public ClassCompilationException(Throwable t) {
        super(t);
    }

    public ClassCompilationException(String message, Throwable t) {
        super(message, t);
    }


}
