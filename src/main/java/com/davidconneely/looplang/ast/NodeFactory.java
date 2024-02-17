package com.davidconneely.looplang.ast;

import java.util.Set;

public final class NodeFactory {
    private NodeFactory() {
    }

    /**
     * :: variable1 `:=` number::0
     * :: variable1 `:=` variable1 `+` number::1
     * :: variable0 `:=` program_name `(` variable1 `,` .. variablen `)`
     */
    public static Node assignment(Set<String> definedPrograms) {
        return new AssignmentNode(definedPrograms);
    }

    /**
     * :: `INPUT` (string1 | variable1) ',' .. (stringn | variablen) [`,`]
     */
    public static Node input() {
        return new InputNode();
    }

    /**
     * :: `OUTPUT` (string1 | variable1) ',' .. (stringn | variablen) [`,`]
     */
    public static Node print() {
        return new PrintNode();
    }

    /**
     * :: `LOOP` variable1 `DO` statement1 ';' .. statementn `END`
     */
    public static Node loop(Set<String> definedPrograms) {
        return new LoopNode(definedPrograms);
    }

    /**
     * :: `PROGRAM` program_name `(` variable1 `,` .. variablen `)` `DO` statement1 ';' .. statementn `END`
     */
    public static Node programDefn(Set<String> definedPrograms) {
        return new ProgramDefnNode(definedPrograms);
    }
}
