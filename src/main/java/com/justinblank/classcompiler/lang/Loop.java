package com.justinblank.classcompiler.lang;

import java.util.List;
import java.util.Objects;

public class Loop implements ElementContainer {

    // nullable--null condition represents an infinite loop
    public final Expression condition;
    public final List<CodeElement> body;

    public Loop(Expression condition, List<CodeElement> body) {
        this.condition = condition;
        this.body = Objects.requireNonNull(body, "body cannot be null");
    }

    public Loop(Expression condition, CodeElement body) {
        this(condition, List.of(body));
    }

    @Override
    public List<CodeElement> getBody() {
        return body;
    }
}
