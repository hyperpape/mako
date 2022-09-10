package com.justinblank.classcompiler.lang;

import java.util.List;

public class Loop implements ElementContainer {

    public final Expression condition;
    public final List<CodeElement> body;

    public Loop(Expression condition, List<CodeElement> body) {
        this.condition = condition;
        this.body = body;
    }

    public Loop(Expression condition, CodeElement body) {
        this(condition, List.of(body));
    }

    @Override
    public List<CodeElement> getBody() {
        return body;
    }
}
