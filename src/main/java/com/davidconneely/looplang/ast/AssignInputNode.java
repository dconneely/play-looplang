package com.davidconneely.looplang.ast;

import com.davidconneely.looplang.interpreter.Context;
import com.davidconneely.looplang.interpreter.InterpreterException;
import com.davidconneely.looplang.lexer.Lexer;
import com.davidconneely.looplang.token.Token;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.stream.Collectors;

import static com.davidconneely.looplang.ast.NodeUtils.nextTokenWithKind;
import static com.davidconneely.looplang.token.Token.Kind.*;

final class AssignInputNode implements Node {
    private String variable;
    private List<Token> printTokens;

    @Override
    public void parse(final Lexer lexer) throws IOException {
        variable = nextTokenWithKind(lexer, IDENTIFIER, "as lvalue variable name in input").textValue();
        nextTokenWithKind(lexer, ASSIGN, "after lvalue in input");
        nextTokenWithKind(lexer, KW_INPUT, "in input").textValue();
        printTokens = PrintNode.nextPrintTokens(lexer);
    }

    @Override
    public void interpret(final Context context) {
        if (variable == null || printTokens == null) {
            throw new InterpreterException("uninitialized input");
        }
        System.out.print(PrintNode.printTokensToText(printTokens, context));
        context.setVariable(variable, Math.max(0, new Scanner(System.in).nextInt()));
    }

    @Override
    public String toString() {
        if (variable == null || printTokens == null) {
            return "<uninitialized input>";
        }
        return variable + " := INPUT " + PrintNode.printTokensToString(printTokens);
    }
}
