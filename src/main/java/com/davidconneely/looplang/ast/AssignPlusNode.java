package com.davidconneely.looplang.ast;

import com.davidconneely.looplang.interpreter.InterpreterContext;
import com.davidconneely.looplang.interpreter.InterpreterException;
import com.davidconneely.looplang.lexer.Lexer;
import com.davidconneely.looplang.parser.ParserException;

import java.io.IOException;
import java.util.Locale;

import static com.davidconneely.looplang.ast.NodeUtils.nextTokenWithKind;
import static com.davidconneely.looplang.token.Token.Kind.*;

final class AssignPlusNode implements Node {
    private String variable; // variable name on left of `:=` sign
    private int number = -1;

    @Override
    public void parse(final Lexer lexer) throws IOException {
        variable = nextTokenWithKind(lexer, IDENTIFIER, "as lvalue variable name in addition").textValue();
        nextTokenWithKind(lexer, ASSIGN, "after lvalue in addition");
        String variable2 = nextTokenWithKind(lexer, IDENTIFIER, "as rvalue variable name in addition").textValue();
        checkVariableIsValid(variable2);
        nextTokenWithKind(lexer, PLUS, "after rvalue variable name in addition");
        number = nextTokenWithKind(lexer, NUMBER, "after plus sign in addition").intValue();
        checkNumberIsValid();
    }

    private void checkVariableIsValid(String variable2) {
        if (!variable.equals(variable2)) {
            throw new ParserException("unmatched variable names in addition; got `" + variable + "` and `" + variable2 + "`");
        }
    }

    public void checkNumberIsValid() {
        if (number != 1) {
            throw new ParserException("expected number added to `" + variable + "` to be `1`; not `" + number + "`.");
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
