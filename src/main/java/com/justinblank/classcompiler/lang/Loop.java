package com.justinblank.classcompiler.lang;

import java.util.List;

public class Loop implements CodeElement {
    private final CodeElement condition;
    private final List<CodeElement> body;

    public Loop(CodeElement condition, List<CodeElement> body) {
        this.condition = condition;
        this.body = body;
    }
}
