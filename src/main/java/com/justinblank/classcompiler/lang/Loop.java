package com.justinblank.classcompiler.lang;

import java.util.List;

public class Loop implements CodeElement {
    public final Expression condition;
    public final List<CodeElement> body;

    public Loop(Expression condition, List<CodeElement> body) {
        this.condition = condition;
        this.body = body;
    }
}
