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
    public static Node newAssignment(Set<String> programs) {
        return new AssignmentNode(programs);
    }

    /**
     * :: `INPUT` (string1 | variable1) ',' .. (stringn | variablen) [`,`]
     */
    public static Node newInput() {
        return new InputNode();
    }

    /**
     * :: `OUTPUT` (string1 | variable1) ',' .. (stringn | variablen) [`,`]
     */
    public static Node newPrint() {
        return new PrintNode();
    }

    /**
     * :: `LOOP` variable1 `DO` statement1 ';' .. statementn `END`
     */
    public static Node newLoop(Set<String> programs) {
        return new LoopNode(programs);
    }

    /**
     * :: `PROGRAM` program_name `(` variable1 `,` .. variablen `)` `DO` statement1 ';' .. statementn `END`
     */
    public static Node newDefinition(Set<String> programs) {
        return new DefinitionNode(programs);
    }
}
