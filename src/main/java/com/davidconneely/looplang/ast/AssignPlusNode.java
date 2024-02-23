package com.davidconneely.looplang.ast;

import com.davidconneely.looplang.interpreter.InterpreterContext;
import com.davidconneely.looplang.interpreter.InterpreterException;
import com.davidconneely.looplang.lexer.Lexer;
import com.davidconneely.looplang.parser.ParserException;
import com.davidconneely.looplang.token.Token;

import java.io.IOException;
import java.util.Locale;

import static com.davidconneely.looplang.ast.NodeUtils.nextTokenWithKind;
import static com.davidconneely.looplang.token.Token.Kind.*;

final class AssignPlusNode implements Node {
    private String variable; // variable name on left of `:=` sign
    private int number = -1;

    @Override
    public void parse(final Lexer lexer) throws IOException {
        variable = nextTokenWithKind(lexer, IDENTIFIER, "as lvalue variable name in addition").value();
        nextTokenWithKind(lexer, ASSIGN, "after lvalue in addition");
        Token token = nextTokenWithKind(lexer, IDENTIFIER, "as rvalue variable name in addition");
        String variable2 = token.value();
        checkVariableIsValid(variable, variable2, token);
        nextTokenWithKind(lexer, PLUS, "after rvalue variable name in addition");
        token = nextTokenWithKind(lexer, NUMBER, "after plus sign in addition");
        number = token.valueInt();
        checkNumberIsValid(number, token);
    }

    private static void checkVariableIsValid(final String variable1, final String variable2, final Token token) {
        if (!variable1.equals(variable2)) {
            throw new ParserException("expected matching variable names in addition; got `" + variable1 + "` and `" + variable2 + "`", token);
        }
    }

    private static void checkNumberIsValid(final int number, final Token token) {
        if (number != 1) {
            throw new ParserException("expected number value of `1` in addition; got " + token, token);
        }
    }

    @Override
    public void interpret(final InterpreterContext context) {
        if (variable == null || number < 0) {
            throw new InterpreterException("uninitialized addition");
        }
        context.setVariable(variable, context.getVariableOrThrow(variable) + number);
    }

    @Override
    public String toString() {
        if (variable == null || number < 0) {
            return "<uninitialized addition>";
        }
        return variable.toLowerCase(Locale.ROOT) + " := " + variable.toLowerCase(Locale.ROOT) + " + " + number;
    }
}
