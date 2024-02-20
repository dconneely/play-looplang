package com.davidconneely.looplang;

import com.davidconneely.looplang.ast.Node;
import com.davidconneely.looplang.interpreter.Context;
import com.davidconneely.looplang.interpreter.Interpreter;
import com.davidconneely.looplang.interpreter.InterpreterFactory;
import com.davidconneely.looplang.lexer.Lexer;
import com.davidconneely.looplang.lexer.LexerFactory;
import com.davidconneely.looplang.parser.Parser;
import com.davidconneely.looplang.parser.ParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public final class Main {
    public static void main(final String[] args) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(Main.class.getResourceAsStream("/Main.loop")), StandardCharsets.UTF_8))) {
            final Lexer lexer = LexerFactory.newLexer(reader);
            final Parser parser = ParserFactory.newParser(lexer);
            final Context context = InterpreterFactory.newGlobalContext();
            final Interpreter interpreter = InterpreterFactory.newInterpreter(context);
            Node node = parser.next();
            while (node != null) {
                interpreter.interpret(node);
                node = parser.next();
            }
        }
    }
}
