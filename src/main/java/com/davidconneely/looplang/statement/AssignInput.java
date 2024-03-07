package com.davidconneely.looplang.statement;

import com.davidconneely.looplang.interpreter.InterpreterContext;
import com.davidconneely.looplang.lexer.Lexer;
import com.davidconneely.looplang.parser.ParserContext;
import com.davidconneely.looplang.token.Token;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import static com.davidconneely.looplang.statement.StatementUtils.nextTokenWithKind;
import static com.davidconneely.looplang.token.Token.Kind.*;

record AssignInput(String variable, List<Token> printTokens) implements Statement {
    static AssignInput parse(final ParserContext context, final Lexer lexer) throws IOException {
        final String variable = nextTokenWithKind(lexer, IDENTIFIER, "as lvalue variable name in input").value();
        nextTokenWithKind(lexer, ASSIGN, "after lvalue in input");
        nextTokenWithKind(lexer, INPUT, "in input");
        final List<Token> printTokens = Print.nextPrintTokens(lexer, "in input arguments");
        return new AssignInput(variable, printTokens);
    }

    @Override
    public void interpret(final InterpreterContext context) {
        System.out.print(Print.printTokensToText(printTokens, context));
        context.setVariable(variable, Math.max(0, new Scanner(System.in).nextInt()));
    }

    @Override
    public String toString() {
        return variable + " := INPUT(" + Print.printTokensToString(printTokens) + ")";
    }
}
