# Mako

![Badge](https://travis-ci.com/hyperpape/mako.svg?branch=main)
![Badge](https://www.repostatus.org/badges/latest/wip.svg)

Mako is a Java DSL that lets you write high level code, then compile
it to Java bytecode and load it, all at runtime.

For example, here is how to specify the recursive fibonacci method:

```
    public static Method fibonacci() {
        var method = new Method(TEST_METHOD, List.of("I"), "I", new GenericVars("x"));
        method.cond(eq(read("x"), 0)).withBody(List.of(
                returnValue(1)));
        method.cond(eq(read("x"), 1)).withBody(List.of(
                returnValue(1)));
        method.returnValue(plus(
                call(TEST_METHOD, Builtin.I, thisRef(), sub(read("x"), 1)),
                call(TEST_METHOD, Builtin.I, thisRef(), sub(read("x"), 2))));
        return method;
    }
```

### Status

This project is pre version 0.1 and has no users as of yet.

### Building 
The compiler requires Java 11. Builds with maven. The generated
classes should work with Java 8.
