
# Mako

![Badge](https://www.repostatus.org/badges/latest/wip.svg)

## Overview 

Mako is a Java library that lets you write high level code then compile it to
Java bytecode and load it, all at runtime. Classes implemented this way are
fully interoperable with standard Java code. That is, they can call regular
objects and methods and implement interfaces or inherit from classes defined 
in regular code. 

To illustrate, here is how to specify the recursive fibonacci method:

```
    public static Method fibonacci() {
        // arguments are method name, arguments types, return type, local variables
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

## Language

Mako has many concepts in common with Java--its constructs compile 
straightforwardly to Java bytecode--but differs in a few ways. In general, it 
tends to implement less functionality than Java itself (i.e. no autoboxing, 
and restricted forms of looping). 

### Basic Expressions and Statements 

Variables have function scope. They are defined by adding them to a `Vars` 
object. 

```java
var methodVars = new GenericVars("x", "y", "z");
```

Variables are always non-final, and may be read/set. Setting a variable is 
a statement (does not produce a value). 

```java
method.set("x", literal(1));
method.get("x");
```

There are no compound assignment operators. You must read a variable, do addition,
then set the variable. 

```java
method.set("x", plus((read("x"), literal(1))));
```

Expressions are written with the operator first:

```java
plus(read("x"), literal(1));
sub(read("x"), 1); // calling literal is usually unnecessary
mul(32, 2.5);
div(32, 2.5);
mod(8, 2);
```

There are standard comparisons that produce booleans:

```java
gt(read("x"), 1);
lt(read("x"), 1);
eq(read("x", 1);
```

Implicit numeric conversions for primitives are supported, but all other 
conversions must be explicit. 

```java
var method = new Method(TEST_METHOD, List.of(), Builtin.L, null);
method.returnValue(1);
```

Method calls put the method name first, followed by the receiver and arguments.
There is a separate method to produce a static call:

```java
call("toString", ReferenceType.of(String.class), read("myString"));
callStatic(CompilerUtil.internalName(Integer.class), "valueOf", 
        ReferenceType.of(Integer.class), literal(0));
```

### Control Flow

TODO

### Defining Methods

TODO

### Defining Classes

TODO

## Building 
The compiler requires Java 11. Builds with maven. The generated
classes should work with Java 8.