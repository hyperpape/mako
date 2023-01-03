package com.justinblank.classcompiler.lang;

import java.util.ArrayList;
import java.util.List;

public class Conditional implements ElementContainer {

    public final Expression condition;
    public List<Conditional> alternates;
    public List<CodeElement> body;

    public Conditional(Expression condition) {
        this.condition = condition;
        this.alternates = new ArrayList<>();
    }

    public Conditional elseif(Expression condition) {
        var cond = new Conditional(condition);
        this.alternates.add(cond);
        return cond;
    }

    public Conditional orElse(CodeElement body) {
        return orElse(List.of(body));
    }

    public Conditional orElse(List<CodeElement> body) {
        var cond = new Conditional(null).withBody(body);
        this.alternates.add(cond);
        return this;
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
