package com.davidconneely.looplang.statement;

import com.davidconneely.looplang.lexer.Lexer;
import com.davidconneely.looplang.parser.ParserException;
import com.davidconneely.looplang.token.Token;
import java.io.IOException;

public final class StatementUtils {
  private StatementUtils() {
    // prevent instantiation.
  }

  public static Token nextTokenWithKind(
      final Lexer lexer, final Token.Kind expected, final String role) throws IOException {
    Token token = lexer.next();
    if (token.kind() != expected) {
      throwUnexpectedParserException(role, token, expected);
    }
    return token;
  }

  public static void throwUnexpectedParserException(final String role, final Token actual, final Token.Kind... expected) {
    final String expectedString = switch (expected.length) {
      case 0 -> "nothing";
      case 1 -> expected[0].toString();
      default -> {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < expected.length; i++) {
          if (i > 0) {
            sb.append(i == expected.length - 1 ? " or " : ", ");
          }
          sb.append(expected[i]);
        }
        yield sb.toString();
      }
    };
    throw new ParserException("expected " + expectedString + " " + role + "; got " + actual, actual);
  }
}
