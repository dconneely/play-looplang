package com.davidconneely.looplang;

import com.davidconneely.looplang.interpreter.Interpreter;
import com.davidconneely.looplang.interpreter.InterpreterContext;
import com.davidconneely.looplang.interpreter.InterpreterFactory;
import com.davidconneely.looplang.lexer.Lexer;
import com.davidconneely.looplang.lexer.LexerFactory;
import com.davidconneely.looplang.lexer.Location;
import com.davidconneely.looplang.parser.Parser;
import com.davidconneely.looplang.parser.ParserContext;
import com.davidconneely.looplang.parser.ParserFactory;
import com.davidconneely.looplang.statement.Statement;
import com.davidconneely.looplang.token.Token;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public final class Main {
    private static final String SOURCEFILE = "/Main.loop";

    public static void main(final String[] args) throws IOException {
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(Main.class.getResourceAsStream(SOURCEFILE)), StandardCharsets.UTF_8))) {
            final Location location = Location.newFile(SOURCEFILE);
            final Lexer lexer = LexerFactory.newLexer(location, reader);
            final ParserContext parserContext = ParserFactory.newContext(location);
            final Parser parser = ParserFactory.newParser(lexer, parserContext, Token.Kind.EOF);
            final InterpreterContext interpreterContext = InterpreterFactory.newGlobalContext(parserContext);
            final Interpreter interpreter = InterpreterFactory.newInterpreter(interpreterContext);
            Statement statement = parser.next();
            while (statement != null) {
                interpreter.interpret(statement);
                statement = parser.next();
            }
        }
    }
}
