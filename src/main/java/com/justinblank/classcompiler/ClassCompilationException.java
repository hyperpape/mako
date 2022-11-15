package com.justinblank.classcompiler;

public class ClassCompilationException extends RuntimeException {

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
