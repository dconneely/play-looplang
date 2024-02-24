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

record AssignNumber(String variable, int number) implements Statement {
    static AssignNumber parse(final ParserContext context, final Lexer lexer) throws IOException {
        final String variable = nextTokenWithKind(lexer, IDENTIFIER, "as lvalue variable name in number assignment").value();
        nextTokenWithKind(lexer, ASSIGN, "after lvalue in number assignment");
        Token token =nextTokenWithKind(lexer, NUMBER, "as rvalue in number assignment");
        final int number = token.valueInt();
        checkNumberIsValid(number, token);
        return new AssignNumber(variable, number);
    }

    private static void checkNumberIsValid(final int number, Token token) {
        if (number < 0) {
            throw new ParserException("expected non-negative value in number assignment; got " + token, token);
        }
    }

    @Override
    public void interpret(final InterpreterContext context) {
        context.setVariable(variable, number);
    }

    @Override
    public String toString() {
        return variable.toLowerCase(Locale.ROOT) + " := " + number;
    }
}
