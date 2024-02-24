package com.davidconneely.looplang.statement;

import com.davidconneely.looplang.interpreter.InterpreterContext;
import com.davidconneely.looplang.lexer.Lexer;
import com.davidconneely.looplang.parser.ParserContext;
import com.davidconneely.looplang.parser.ParserException;
import com.davidconneely.looplang.token.Token;

import java.io.IOException;
import java.util.Locale;

import static com.davidconneely.looplang.statement.StatementUtils.nextTokenWithKind;
import static com.davidconneely.looplang.token.Token.Kind.*;

record AssignIncrement(String variable) implements Statement {
    static AssignIncrement parse(final ParserContext context, final Lexer lexer) throws IOException {
        final String variable = nextTokenWithKind(lexer, IDENTIFIER, "as lvalue variable name in increment").value();
        nextTokenWithKind(lexer, ASSIGN, "after lvalue in increment");
        Token token = nextTokenWithKind(lexer, IDENTIFIER, "as rvalue variable name in increment");
        String variable2 = token.value();
        checkVariableIsValid(variable, variable2, token);
        nextTokenWithKind(lexer, PLUS, "after rvalue variable name in increment");
        token = nextTokenWithKind(lexer, NUMBER, "after plus sign in increment");
        final int number = token.valueInt();
        checkNumberIsValid(number, token);
        return new AssignIncrement(variable);
    }

    private static void checkVariableIsValid(final String variable1, final String variable2, final Token token) {
        if (!variable1.equals(variable2)) {
            throw new ParserException("expected matching variable names in increment; got `" + variable1 + "` and `" + variable2 + "`", token);
        }
    }

    private static void checkNumberIsValid(final int number, final Token token) {
        if (number != 1) {
            throw new ParserException("expected number value of `1` in increment; got " + token, token);
        }
    }

    @Override
    public void interpret(final InterpreterContext context) {
        context.setVariable(variable, context.getVariableOrThrow(variable) + 1);
    }

    @Override
    public String toString() {
        return variable.toLowerCase(Locale.ROOT) + " := " + variable.toLowerCase(Locale.ROOT) + " + 1";
    }
}
