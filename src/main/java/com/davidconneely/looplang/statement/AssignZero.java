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

record AssignZero(String variable) implements Statement {
    static AssignZero parse(final ParserContext context, final Lexer lexer) throws IOException {
        final String variable = nextTokenWithKind(lexer, IDENTIFIER, "as lvalue variable name in zero assignment").value();
        nextTokenWithKind(lexer, ASSIGN, "after lvalue in zero assignment");
        Token token =nextTokenWithKind(lexer, NUMBER, "as rvalue in zero assignment");
        final int number = token.valueInt();
        checkNumberIsValid(number, token);
        return new AssignZero(variable);
    }

    private static void checkNumberIsValid(final int number, Token token) {
        if (number != 0) {
            throw new ParserException("expected `0` in zero assignment; got " + token, token);
        }
    }

    @Override
    public void interpret(final InterpreterContext context) {
        context.setVariable(variable, 0);
    }

    @Override
    public String toString() {
        return variable.toLowerCase(Locale.ROOT) + " := 0";
    }
}
