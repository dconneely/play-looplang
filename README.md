# Toy Language

This is an implementation of a toy language based on the
[LOOP language](https://en.wikipedia.org/wiki/LOOP_%28programming_language%29)
which allows primitive recursive functions.

This is the extended definition, which includes how to define and call functions,
allows for comments, and to log values to the console.

    <prog> ::= 'stz' <varname>
            |  'inc' <varname>
            |  'for' (<varname>|<number>) '(' <prog> ')'
            |  <prog> (';'|<newline>) <prog>
            |  <procname> (<varname>|<number>)*
            |  'def' <procname> <varname>* '(' prog ')'
            |  'println' (<varname>|<number>|<string>)*
            |  '#' <comment-is-rest-of-line>

where `<varname>` and `<procname>` are valid Java identifiers,`<number>` is a
numeric (non-negative integer) literal, and `<string>` is a double-quoted
string (following Java conventions).

Additionally, the semantics of a `<proc>` procedure call are that parameters
are passed by reference, and the procedure body runs in a new context for
variables, so a procedure can only change variables in the calling context
that have been used as parameters to the procedure.

For example,

    stz x; inc x; inc x;  # x:=2
    stz y; inc y; inc y;  # y:=2
    stz z; inc z; inc z;  # z:=2
    # the `x` in the body of `proc1` is a local variable:
    def proc1 a b (stz a; stz b; stz x);
    proc1 y z;  # x=2, y=0, z=0

Despite the fact that the only non-control statements are `stz` (set to zero)
and `inc` (increment), many complicated operations are possible.

For example,

    def add x y (for y (inc x))  # x := x + y
    def truth x y (stz x; for y (stz x; inc x))  # x := (y == 0) ? 0 : 1
    def not x y (stz x; inc x; for y (stz x))  # x := (y != 0) ? 0 : 1
    def assign x y (stz x; add x y)  # x = y
    def swap x y (assign z x; assign x y; assign y z)  # x,y := y,x
    def dec x (stz y; for x (assign z y; inc y); assign x z)  # x := x - 1
    def sub x y (for y (dec x))  # x := x - y
    # relational operators, division, etc. all possible
