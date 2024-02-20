package com.davidconneely.looplang.ast;

import com.davidconneely.looplang.interpreter.Context;
import com.davidconneely.looplang.interpreter.InterpreterException;
import com.davidconneely.looplang.lexer.Lexer;
import com.davidconneely.looplang.parser.ParserException;
import com.davidconneely.looplang.token.Token;

import java.io.IOException;
import java.util.Locale;

import static com.davidconneely.looplang.ast.NodeUtils.nextTokenWithKind;
import static com.davidconneely.looplang.token.Token.Kind.*;

final class AssignNumberNode implements Node {
    private String variable; // variable name on left of `:=` sign
    private int number = -1;

    @Override
    public void parse(final Lexer lexer) throws IOException {
        variable = nextTokenWithKind(lexer, IDENTIFIER, "as lvalue variable name in number assignment").textValue();
        nextTokenWithKind(lexer, ASSIGN, "after lvalue in number assignment");
        number = nextTokenWithKind(lexer, NUMBER, "as rvalue in number assignment").intValue();
        checkNumberIsValid();
    }

    private void checkNumberIsValid() {
        if (number < 0) {
            throw new ParserException("expected number assigned to `" + variable + "` to be `0` or above; not `" + number + "`.");
        }
    }

    @Override
    public void interpret(final Context context) {
        if (variable == null || number < 0) {
            throw new InterpreterException("uninitialized number assignment");
        }
        context.setVariable(variable, number);
    }

    @Override
    public String toString() {
        if (variable == null || number < 0) {
            return "<uninitialized number assignment>";
        }
        return variable.toLowerCase(Locale.ROOT) + " := " + number;
    }
}
