package com.davidconneely.looplang.statement;

import com.davidconneely.looplang.lexer.Lexer;
import com.davidconneely.looplang.parser.ParserContext;

import java.io.IOException;

public final class StatementFactory {
    private StatementFactory() {
    }

    /**
     * :: variable0 `:=` number0
     */
    public static Statement newAssignNumber(final ParserContext context, final Lexer lexer) throws IOException {
        return AssignZero.parse(context, lexer);
    }

    /**
     * :: variable0 `:=` variable0 `+` number0
     */
    public static Statement newAssignPlus(final ParserContext context, final Lexer lexer) throws IOException {
        return AssignIncrement.parse(context, lexer);
    }

    /**
     * :: variable0 `:=` `INPUT` (string1|number1|variable1) ',' .. (stringn|numbern|variablen)
     */
    public static Statement newAssignInput(final ParserContext context, final Lexer lexer) throws IOException {
        return AssignInput.parse(context, lexer);
    }

    /**
     * :: variable0 `:=` program_name `(` variable1 `,` .. variablen `)`
     */
    public static Statement newAssignCall(final ParserContext context, final Lexer lexer) throws IOException {
        return AssignCall.parse(context, lexer);
    }

    /**
     * :: `PRINT` (string1|number1|variable1) ',' .. (stringn|numbern|variablen)
     */
    public static Statement newPrint(final ParserContext context, final Lexer lexer) throws IOException {
        return Print.parse(context, lexer);
    }

    /**
     * :: `LOOP` variable1 `DO` statement1 ';' .. statementn `END`
     */
    public static Statement newLoop(final ParserContext context, final Lexer lexer) throws IOException {
        return Loop.parse(context, lexer);
    }

    /**
     * :: `PROGRAM` program_name `(` variable1 `,` .. variablen `)` `DO` statement1 ';' .. statementn `END`
     */
    public static Statement newDefinition(final ParserContext context, final Lexer lexer) throws IOException {
        return Definition.parse(context, lexer);
    }
}
