package com.davidconneely.looplang;

import com.davidconneely.looplang.interpreter.Interpreter;
import com.davidconneely.looplang.interpreter.InterpreterContext;
import com.davidconneely.looplang.interpreter.InterpreterFactory;
import com.davidconneely.looplang.lexer.CodepointIterator;
import com.davidconneely.looplang.lexer.Lexer;
import com.davidconneely.looplang.lexer.LexerFactory;
import com.davidconneely.looplang.lexer.Location;
import com.davidconneely.looplang.parser.Parser;
import com.davidconneely.looplang.parser.ParserContext;
import com.davidconneely.looplang.parser.ParserFactory;
import com.davidconneely.looplang.statement.Statement;
import com.davidconneely.looplang.token.Token;
import java.io.IOException;
import java.nio.file.Path;

public final class Main {
  private static final String DEFAULT_RESOURCE = "/Main.loop";

  public static void main(final String[] args) throws IOException {
    if (args.length > 1) {
      IO.println("Usage: looplang [source-file]");
      System.exit(1);
    }

    final String sourceName;
    final CodepointIterator codepoints;

    if (args.length == 1) {
      Path filePath = Path.of(args[0]);
      sourceName = filePath.toString();
      codepoints = LexerFactory.newCodepointIterator(filePath);
    } else {
      sourceName = DEFAULT_RESOURCE;
      codepoints =
          LexerFactory.newCodepointIterator(Main.class.getResourceAsStream(DEFAULT_RESOURCE));
    }

    final Location location = Location.newFile(sourceName);
    try (Lexer lexer = LexerFactory.newLexer(location, codepoints)) {
      final ParserContext parserContext = ParserFactory.newContext(location);
      final Parser parser = ParserFactory.newParser(lexer, parserContext, Token.Kind.EOF);
      final InterpreterContext interpreterContext =
          InterpreterFactory.newGlobalContext(parserContext);
      final Interpreter interpreter = InterpreterFactory.newInterpreter(interpreterContext);
      Statement statement = parser.next();
      while (statement != null) {
        interpreter.interpret(statement);
        statement = parser.next();
      }
    }
  }
}
