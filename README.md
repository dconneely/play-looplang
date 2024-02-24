# `play-looplang`

This is an implementation of a toy language based on the
[LOOP programming language](https://en.wikipedia.org/wiki/LOOP_%28programming_language%29)
which allows primitive recursive functions.

### Syntax

The statements supported in the language are very limited (in LOOP only bound loops can be expressed):

A _statement_ can be any of:

1. _varname_` := 0`
2. _varname_` := `_varname_` + 1`
3. _statement_`; `_statement_
4. `LOOP `_varname_` DO `_statement_` END`

Note the `LOOP` statement executes a finite number of times (it evaluates _varname_ once before starting the loop, not
on each iteration).

Additionally, we define a few minor additional statements that are not on the Wikipedia page for convenience:

5. `PRINT(`_comma-separated list of strings, numbers, variables_`)`

   Outputs the values on the same line (strings are output as their literal value, numbers are output as their literal
   value, variables are output as their assigned value or `undefined` if the variable has not been assigned). The
   statement also outputs a line separator at the end of the values (or if there aren't any).

6. _varname_` := INPUT(`_comma-separated list of strings, numbers, variables_`)`

   Sets the variable to a non-negative integer value entered by the user. Strings, numbers and variables are output as 
   by the `PRINT` statement as a prompt (the prompt `?` will be used if none is supplied).

7. `PROGRAM `_progname_`(`_comma-separated list of parameter variables_`) DO `_statement_` END`

   Allows convenience instructions to be defined. These can only be defined at the top-level (i.e. a definition cannot
   define a nested convenience instruction). Also, the statements in the convenience instruction cannot refer to other
   convenience instructions that have not yet been defined (including the current one) to prevent unbounded loops by
   recursion, and any variable references are either parameters or locally-scoped (i.e. no side-effects).

8. _varname_` := `_progname_`(`_comma-separated list of argument variables_`)`

   The only way to use these convenience instructions is with this statement, which treats the call to the convenience
   instruction like a function call (in other languages) with the restrictions on references and variables mentioned in
   the previous paragraph. If a value is assigned to the special variable named `x0` within the called program, then
   this value is what gets assigned to the caller's _varname_ variable when the called program ends (otherwise
   _varname_ will receive the value `0`).

### Examples

The convention of `x0` as the "return value" from a `PROGRAM` allows the examples on the
[Wikipedia page](https://en.wikipedia.org/wiki/LOOP_%28programming_language%29) to be transcribed fairly directly.

For example,

```
 PROGRAM ASSIGN(x1) DO   |  PROGRAM MULT(x1, x2) DO   |  PROGRAM PRED(x1) DO
   x0 := 0;              |    x0 := 0;                |    x2 := 0;
   LOOP x1 DO            |    LOOP x2 DO              |    LOOP x1 DO
     x0 := x0 + 1        |      x0 := ADD(x1, x0)     |      x0 : = ASSIGN(x2);
   END                   |    END                     |      x2 := x2 + 1
 END;                    |  END;                      |    END END;
                         |                            |
-------------------------|----------------------------|--------------------------
                         |                            |
 PROGRAM ADD(x1, x2) DO  |  PROGRAM POWER(x1, x2) DO  |  PROGRAM DIFF(x1, x2) DO
   x0 = ASSIGN(x1);      |    x0 := 0; x0 := x0 + 1   |    x0 := ASSIGN(x1);
   LOOP x2 DO            |    LOOP x2 DO              |    LOOP x2 DO
     x0 := x0 + 1        |      x0 := MULT(x1, x0)    |      x0 := PRED(x0)
   END                   |    END                     |    END
 END;                    |  END;                      |  END;
```

### Limitations / rules

* Note there is no overloading of `PROGRAM` names based on number of parameters or anything like that.
* Programs can be defined only once, and program definitions cannot be nested.
* A statement can only refer to (i.e. call) programs that have been fully-defined textually-before the statement.
* Variables cannot be referred to before they are defined (except `x0` in a `PROGRAM` which is initialized to `0`, or
  in a `PRINT` statment where they will be output as `undefined`).
* The only data type is the non-negative integer (which is the data type of all variables, and the return type of all
  programs).
* The parentheses around arguments lists (in statements 5., 6., 7., 8.) are required.
* The `;` statement separator is generally optional.
* The `DO` keyword, in the `LOOP` and `PROGRAM` statements, is generally optional.

### Roadmap

* [X] Improve lexer and parser syntax error-handling (line and column numbers).
* [X] More consistent handling of parentheses in parameter / argument lists.
* [X] `Node` classes are not really AST nodes, they are just language statements - may be better to call them this.
* [X] Separate parsing and interpreting. Parsing should construct valid `Node` objects, rather than create invalid
      empty objects and populate them later. `Node` attributes could then all be immutable.
* [X] Improve the context classes: make them used more consistently.
* [ ] Improve interpreter runtime error-handling (line and column number).
* [ ] Report errors nicely to end user with source line arrows, etc.
* [ ] `InterpreterContext` subclasses should work better without chaining.
* [ ] Yikes! Need more unit tests. Need more integration tests (e.g. test scripts)
* [ ] Optimize `LOOP` statements? (e.g. idempotent content of loop become an `if` instead of `for`; well-known loop
      content)
* [ ] Add more items to this roadmap!