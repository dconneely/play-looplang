package com.davidconneely.looplang;

import java.io.*;
import java.nio.charset.StandardCharsets;

import com.davidconneely.looplang.ast.Node;
import com.davidconneely.looplang.lexer.Lexer;
import com.davidconneely.looplang.interpreter.Context;
import com.davidconneely.looplang.interpreter.Interpreter;
import com.davidconneely.looplang.interpreter.InterpreterFactory;
import com.davidconneely.looplang.lexer.LexerFactory;
import com.davidconneely.looplang.parser.Parser;
import com.davidconneely.looplang.parser.ParserFactory;

public final class Main {
    public static void main(final String[] args) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Main.class.getResourceAsStream("/Main.loop"), StandardCharsets.UTF_8))) {
            final Lexer tokens = LexerFactory.lexer(reader);
            final Parser parser = ParserFactory.newParser(tokens);
            final Context context = InterpreterFactory.newGlobalContext();
            final Interpreter interpreter = InterpreterFactory.newInterpreter(context);
            while (true) {
                Node node = parser.next();
                if (node == null) {
                    break;
                }
                interpreter.interpret(node);
            }
        }
    }
}
