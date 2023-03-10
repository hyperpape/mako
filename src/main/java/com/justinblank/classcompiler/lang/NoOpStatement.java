package com.justinblank.classcompiler.lang;

/**
 * Does nothing whatsoever--does not emit a noop bytecode, but just does nothing.
 *
 * Having this class allows a branch to more easily add a meaningful statement to a method or not, depending on desired
 * behavior:
 *
 * e.g. loop(expression).withBody(List.of(statement1, someConditional ? expression2 : new NoOpStatement());
 */
public class NoOpStatement extends Statement {
}
