package com.justinblank.classcompiler.lang;

import java.util.List;

public class Conditional implements CodeElement {

    private final Conditional parent;
    private final Expression condition;
    private boolean finished = false;
    private List<CodeElement> body;

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
}
