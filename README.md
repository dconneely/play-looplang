# task-looplang

This is an implementation of a toy language based on the
[LOOP programming language](https://en.wikipedia.org/wiki/LOOP_%28programming_language%29)
which allows primitive recursive functions.

The statements supported in the language are very limited (in LOOP only bound loops can be expressed):

A _statement_ can be any of:
1. _varname_ ` := 0`
2. _varname_ ` := ` _varname_ ` + 1`
3. _statement_ `; ` _statement_
4. `LOOP ` _varname_ ` DO ` _statement_ ` END`

Note the `LOOP` statement executes a finite number of times (it evaluates _varname_ once before starting the loop, not
on each iteration).

Additionally, we define a few minor additional statements that are not on the Wikipedia page for convenience:

5. `INPUT ` _<comma-separated list of strings and variables>_
6. `PRINT ` _<comma-separated list of strings and variables>_
7. `PROGRAM ` _progname_ `(` _<comma-separated list of parameters>_ `) DO ` _statement_ ` END`
8. _varname_ ` := ` _progname_ `(` _<comma-separated list of arguments>_ `)`

`INPUT` (#5) must include at least one variable name which will be prompted for and a non-negative integer value can be
entered by the user. String literals are displayed as prompts (the prompt `?` will be used if none is supplied).

`PRINT` (#6) just outputs the values on the same line (strings are output as their literal value, variables are output
as their assigned value or `undefined` if the variable has not been assigned). Unless there is a trailing comma, the
statement will also output a line separator at the end of the values.

`PROGRAM` (#6) allows convenience instructions to be defined. These can only be defined at the top-level (i.e. a
definition cannot define a nested convenience instruction). Also, the statements in the convenience instruction cannot
refer to other convenience instructions that have not yet been defined (including the current one) to prevent unbounded
loops by recursion, and any variable references are either parameters or locally-scoped (i.e. no side-effects).

The only way to use these convenience instructions is with statement #8, which treats the call to the convenience
instruction like a function call in other languages (with the restrictions on references and variables mentioned in the
previous paragraph). If the program assigns a value to the special variable named `x0`, then this value is assigned to
the _varname_ when the program ends (otherwise _varname_ will receive the value `0`). In this way, some of the example
programs on the Wikipedia page can be transcribed fairly directly.

For example,

```
PROGRAM ASSIGN(x1) DO  | PROGRAM MULT(x1, x2) DO  | PROGRAM PRED(x1) DO
  x0 := 0;             |   x0 := 0;               |   x2 := 0;
  LOOP x1 DO           |   LOOP x2 DO             |   LOOP x1 DO
    x0 := x0 + 1       |     x0 := ADD(x1, x0)    |     x0 : = ASSIGN(x2);
  END                  |   END                    |     x2 := x2 + 1
END                    | END                      |   END
                       |                          | END
-----------------------|--------------------------|-------------------------
PROGRAM ADD(x1, x2) DO | PROGRAM POWER(x1, x2) DO | PROGRAM DIFF(x1, x2) DO
  x0 = ASSIGN(x1);     |   x0 := 0; x0 := x0 + 1  |   x0 := ASSIGN(x1);
  LOOP x2 DO           |   LOOP x2 DO             |   LOOP x2 DO
    x0 := x0 + 1       |     x0 := MULT(x1, x0)   |     x0 := PRED(x0)
  END                  |   END                    |   END
END                    | END                      | END
```

Note there is no overloading based on number of parameters or anything like that, and the only data type is the
non-negative integer (which is the data type of all variables, and the return type of all programs).

The `;` statement separator and the `DO` keyword in the `LOOP` and `PROGRAM` statements are both generally optional.