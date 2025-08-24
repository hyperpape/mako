

# Mako

![Badge](https://www.repostatus.org/badges/latest/wip.svg)

## Overview 

Mako is a Java code generation library that produces Java classes/bytecode.

Mako features a high level DSL for defining methods/classes, with a focus on supporting complex or algorithmic code
(loops, conditionals, switches, etc).

Mako supports generating and loading classes at runtime, or precompiling `.class` 
files that can be packaged with an application.

Classes implemented via Mako are fully interoperable with standard Java code. That is, they can call regular objects
and methods and implement interfaces or inherit from classes defined in regular code.

To illustrate, here is how to specify the recursive fibonacci method:

```
        var classBuilder = new ClassBuilder("fibonacci2", "");
        classBuilder.addEmptyConstructor();
        var method = classBuilder.mkMethod("fibonacci", List.of("I"), "I", new GenericVars("x"));
        method.cond(eq(read("x"), 0)).withBody(List.of(
                returnValue(1)));
        method.cond(eq(read("x"), 1)).withBody(List.of(
                returnValue(1)));
        method.returnValue(plus(
                CodeElement.call("fibonacci", Builtin.I, thisRef(), sub(read("x"), 1)),
                CodeElement.call("fibonacci", Builtin.I, thisRef(), sub(read("x"), 2))));
        Class<?> cls = new ClassCompiler(classBuilder).generateClass();
        o = cls.getDeclaredConstructors()[0].newInstance();
        System.out.println(o.getClass().getDeclaredMethod("fibonacci", int.class).invoke(o, 5)); // prints 8
```

### Status

This project is pre version 0.1. It has been used to reimplement
[needle](https://github.com/hyperpape/needle/) using mako, and
supports the features necessary for that, but there are probably
missing constructs or APIs that need more work.

Experiments using mako, feedback/suggestions, and bug reports are
welcome.

## Language

Semantically, Mako has many concepts in common with Java--its constructs compile 
straightforwardly to Java bytecode--but differs in a few ways. In general, it 
tends to implement less functionality than Java itself (i.e. no autoboxing, 
and fewer forms of looping).

Syntactically, Mako uses prefix notation (operators like `eq` or `plus` come
before their arguments), like a lisp, but freely mixes method calls and static
methods, according to what I find most natural to write. Many language
constructs can be created using static methods, or fluent method calls off of
the appropriate object (either a `Method` or `CodeElement` or the proper type).

### Basic Expressions and Statements

Variables have function scope. They are defined by adding them to a `Vars` 
object.

```java
Vars methodVars = new GenericVars("x", "y", "z");
// alternately
methodVars.add("a");
methodVars.add("b");
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

Arithmetic and equality expressions are written with the operator first:

```java
plus(read("x"), literal(1));
sub(read("x"), 1); // calling literal is usually unnecessary in arithmetic contexts
mul(32, 2.5);
div(32, 2.5);
mod(8, 2);
```

There are standard comparisons that produce booleans:

```java
gt(2, 1); // true
gte(1, 1); // true
lt(0, 1); // true
lte(0, 0); // true
eq(1, 1); // true
neq(1, 2); // true
```

Implicit numeric conversions for primitives are supported, but all other 
conversions must be explicit (no auto-boxing).

```java
// define a method returning a long
Method method = new Method("returnsLong", List.of(), Builtin.L, null);
method.returnValue(1);
```

Method calls put the method name first, followed by the receiver and arguments.
There is a separate method to produce a static call:

```java
call("toString", ReferenceType.of(String.class), read("myString"));
callStatic(CompilerUtil.internalName(Integer.class), "valueOf", 
        ReferenceType.of(Integer.class), literal(0));
```

The construct method is used for constructing new object instances.

```java
construct(ReferenceType.of(Integer.class), literal(16);
```

### Arrays

Standard array operations are supported. Array types can be constructed with ArrayType#of().

```java

newArray(5, Builtin.I); // equivalent to int[5];
// note that we use Builtin.I, not ArrayType.of(Builtin.i), which would create int[][5];
arraySet(arrayExpression, 0, "abc"); // sets index 0 to "abc"
arrayRead(arrayExpression, 0); // retrieves the value at index 0;
arrayLength(arrayExpression); // equivalent of arrayExpression.length;
```

Note that `arraySet` is a statement, not an expression.

### Conditionals

Conditionals are created using `cond`, then `withBody`. `else if` is done with the `elseif` method on a conditional. 
Else blocks can be added with `orElse`. 

```java
method.cond(eq(read("i"), 3))
.withBody(List.of(returnValue(3)))
.elseIf(eq(read("i"), 4)).withBody(list.of(returnValue(4)));
.orElse().withBody(List.of(returnValue(5)));
```

### Loops

Mako has only while loops for now.

```java
        var method = new Method("loopMethod", List.of(), Builtin.I, new GenericVars("a"));
        method.set("a", 1);
        method.loop(lt(read("a"), 5),
                List.of(set("a", plus(read("a"), 1))));
        method.returnValue(read("a"));
```

Loops support `break` and `continue`, spelled `escape` and `skip`.

```java
method.set("a", 1);
        method.loop(lt(read("a"), 5),
                List.of(set("a", plus(read("a"), 1)),
                        escape()));
```

```java
method.loop(lt( read("a"), 5),
    List.of(set("a", plus(read("a"), 1)),
        skip()));
```

### Defining Methods

```java

Vars vars = new GenericVars("a", "b", "c", "d");
// Arguments to the method constructor are name, arguments, return type, the variables
Method method = new Method("argumentDemo", List.of(Builtin.I), Builtin.I, vars);
method.set("d", 2);
method.returnValue(read("d"));
```

Although the vars object is a map, the order in which vars are defined is tracked. Arguments to the method are
automatically assigned to the initial elements of the Vars array. So in the example above, the integer argument to
`argumentDemo` will be assigned to `a` at the beginning of the method.

### Defining Classes

To define a class, create a `ClassBuilder`, add methods to it, then pass it to a `ClassCompiler`. 

```java
        // arguments are className, package, superclass, interfaces
        ClassBuilder cb = new ClassBuilder("TestClass", "", "java/lang/Object", new String[]{});
        cb.addEmptyConstructor();
        Method method = cb.mkMethod("foo", List.of("I"), "I", new GenericVars());
        Class<?> cls = new ClassCompiler(cb).generateClass();
```

## Building 
The compiler requires Java 11, and builds with maven. The generated
classes should work with Java 8.

## Mako, ByteBuddy and ASM

Mako is built on top of [ASM](https://asm.ow2.io/), and provides a subset of 
ASM's capabilities. In particular, Mako only provides an API for generating
classes from scratch, it does not provide an API for transforming them. What 
Mako attempts to add is a concise, higher level API for generating algorithmic
code at runtime. For an example where this can be useful, see 
https://github.com/hyperpape/temporalformats/.

Compared to ByteBuddy, Mako is more verbose for simple use-cases, but 
handles some use-cases where the ByteBuddy based solution would be to use ASM.