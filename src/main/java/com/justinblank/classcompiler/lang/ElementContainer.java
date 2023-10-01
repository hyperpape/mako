package com.justinblank.classcompiler.lang;

import java.util.List;

/**
 * Interface for constructs like loops that may contain expressions that produce a value, but where the value is not
 * naturally consumed. For such constructs, we may need to pop the values.
 *
 * // TODO: shouldn't switch implement this interface?
 *
 */
public interface ElementContainer extends CodeElement {

    List<CodeElement> getBody();
}
