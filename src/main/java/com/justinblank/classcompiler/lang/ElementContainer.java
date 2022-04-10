package com.justinblank.classcompiler.lang;

import java.util.List;

public interface ElementContainer extends CodeElement {

    List<CodeElement> getBody();
}
