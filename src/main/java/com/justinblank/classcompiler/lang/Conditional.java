package com.justinblank.classcompiler.lang;

import java.util.List;

public class Conditional implements ElementContainer {

    public final Conditional parent;
    public final Expression condition;
    private boolean finished = false;
    public List<CodeElement> body;

    public Conditional(Expression condition) {
        this.condition = condition;
        this.parent = null;
    }

    Conditional(Expression condition, Conditional root) {
        this.condition = condition;
        this.parent = root;
    }

    public Conditional elseif(Expression condition) {
        return new Conditional(condition, this);
    }

    public Conditional orElse() {
        var cond = new Conditional(null, this.parent != null ? this.parent : this);
        cond.parent.finished = true;
        return cond;
    }

    public Conditional withBody(List<CodeElement> placeholders) {
        this.body = placeholders;
        return this;
    }

    public Conditional withBody(CodeElement placeholder) {
        this.body = List.of(placeholder);
        return this;
    }

    public List<CodeElement> getBody() {
        return body;
    }
}
