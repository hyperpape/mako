package com.justinblank.classcompiler.lang;

import com.justinblank.classcompiler.ClassCompilationException;
import org.junit.Test;

import static com.justinblank.classcompiler.lang.TestSyntax.apply;
import static org.junit.Assert.assertThrows;

public class TestCompilerErrors {

    @Test
    public void testIncorrectReturnType() {
        assertThrows(ClassCompilationException.class, () -> {
            apply(CompilerErrorTestMethods.typeMismatchReturnValue());
        });
    }
}
