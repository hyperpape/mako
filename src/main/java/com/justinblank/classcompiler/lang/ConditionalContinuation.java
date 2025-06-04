package com.justinblank.classcompiler.lang;

import java.util.List;

public class ConditionalContinuation {

    private Conditional main;
    private Conditional target;

    public ConditionalContinuation(Conditional main, Conditional target) {
        this.main = main;
        this.target = target;
    }

    public Conditional withBody(List<CodeElement> codeElements) {
        target.withBody(codeElements);
        return main;
    }
}
