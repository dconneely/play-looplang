package com.davidconneely.looplang.lexer;

import static com.davidconneely.looplang.token.TokenFactory.TOK_ASSIGN;
import static com.davidconneely.looplang.token.TokenFactory.TOK_COMMA;
import static com.davidconneely.looplang.token.TokenFactory.TOK_EOF;
import static com.davidconneely.looplang.token.TokenFactory.TOK_LPAREN;
import static com.davidconneely.looplang.token.TokenFactory.TOK_PLUS;
import static com.davidconneely.looplang.token.TokenFactory.TOK_RPAREN;
import static com.davidconneely.looplang.token.TokenFactory.TOK_SEMICOLON;

import com.davidconneely.looplang.token.Token;
import com.davidconneely.looplang.token.TokenFactory;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;

final class DefaultLexer implements Lexer {
  private enum LexState {
    LEX_INITIAL,
    LEX_IN_COMMENT_LINE,
    LEX_IN_STRING_LITERAL,
    LEX_IN_NUMERIC_LITERAL,
    LEX_IN_IDENTIFIER
  }

  private final Location location;
  private final CodepointIterator codepoints;
  private final Deque<Token> lookahead;

  DefaultLexer(final Location location, final CodepointIterator codepoints) {
    this.location = location;
    this.codepoints = codepoints;
    this.lookahead = new ArrayDeque<>();
  }

  @Override
  public void pushback(final Token token) {
    lookahead.push(token);
  }

  @Override
  public boolean hasNext() {
    Token t = next();
    if (t.kind() == Token.Kind.EOF) {
      return false;
    }
    pushback(t);
    return true;
  }

  @Override
  public Token next() {
    if (!lookahead.isEmpty()) {
      return lookahead.pop();
    }
    LexState state = LexState.LEX_INITIAL;
    final StringBuilder valBuf = new StringBuilder();
    while (true) {
      if (!codepoints.hasNext()) {
        return switch (state) {
          case LEX_INITIAL -> TOK_EOF;
          case LEX_IN_COMMENT_LINE -> TOK_EOF; // comments at EOF are fine.
          case LEX_IN_STRING_LITERAL ->
              throw new LexerException("unterminated string literal at end of file", location);
          case LEX_IN_NUMERIC_LITERAL -> TokenFactory.newNumber(valBuf.toString()).at(location);
          case LEX_IN_IDENTIFIER ->
              TokenFactory.newIdentifierOrKeyword(valBuf.toString().toUpperCase(Locale.ROOT))
                  .at(location);
        };
      }
      final int ch1 = codepoints.nextCodepoint();
      int ch2;
      switch (state) {
        case LEX_INITIAL -> {
          location.nextToken();
          switch (ch1) {
            case '\r' -> {
              if (codepoints.hasNext()) {
                ch2 = codepoints.nextCodepoint();
                if (ch2 != '\n') {
                  codepoints.pushback(ch2);
                  throw new LexerException(
                      "standalone `CR` control character (not followed by `LF`)", location);
                }
              }
              location.nextLine();
            }
            case '\n' -> location.nextLine();
            case ':' -> {
              if (codepoints.hasNext()) {
                ch2 = codepoints.nextCodepoint();
                if (ch2 != '=') {
                  codepoints.pushback(ch2);
                  throw new LexerException(
                      "colon not followed by `=`, but `"
                          + Character.toString(ch2)
                          + "` ("
                          + ch2
                          + ")",
                      location);
                }
              } else {
                throw new LexerException("colon not followed by `=` at end of file", location);
              }
              location.extendToken();
              return TOK_ASSIGN.at(location);
            }
            case '+' -> {
              return TOK_PLUS.at(location);
            }
            case '(' -> {
              return TOK_LPAREN.at(location);
            }
            case ')' -> {
              return TOK_RPAREN.at(location);
            }
            case ',' -> {
              return TOK_COMMA.at(location);
            }
            case ';' -> {
              return TOK_SEMICOLON.at(location);
            }
            case '#' -> state = LexState.LEX_IN_COMMENT_LINE;
            case '\"' -> state = LexState.LEX_IN_STRING_LITERAL;
            default -> {
              if (Character.isDigit(ch1)) {
                state = LexState.LEX_IN_NUMERIC_LITERAL;
                valBuf.appendCodePoint(ch1);
              } else if (Character.isJavaIdentifierStart(ch1)) {
                state = LexState.LEX_IN_IDENTIFIER;
                valBuf.appendCodePoint(ch1);
              } else if (!Character.isWhitespace(ch1)) {
                throw new LexerException(
                    "unrecognised symbol `" + Character.toString(ch1) + "` (" + ch1 + ")",
                    location);
              }
              // ignore whitespace when in LEX_INITIAL state.
            }
          }
        }
        case LEX_IN_COMMENT_LINE -> {
          location.extendToken();
          if (ch1 == '\n') { // end of the line comment.
            location.nextLine();
            state = LexState.LEX_INITIAL;
          }
          // consume comment
        }
        case LEX_IN_STRING_LITERAL -> {
          location.extendToken();
          if (ch1 == '\\') {
            if (!codepoints.hasNext()) {
              throw new LexerException(
                  "unterminated string literal after escape at end of file", location);
            }
            ch2 = codepoints.nextCodepoint();
            location.extendToken();
            switch (ch2) {
              case 't' -> valBuf.append('\t');
              case 'n' -> valBuf.append('\n');
              case 'r' -> valBuf.append('\r');
              case '"', '\\' -> valBuf.append((char) ch2);
              default ->
                  throw new LexerException(
                      "unexpected string escape character (#" + ch2 + ")", location);
            }
          } else if (ch1 == '\n') {
            Location endOfLine = Location.copyOf(location);
            location.nextLine();
            throw new LexerException("missing closing quote on string literal", endOfLine);
          } else if (ch1 != '"') {
            valBuf.appendCodePoint(ch1);
          } else {
            // we want to consume the '"' as part of the string, so no pushback.
            final String val = valBuf.toString();
            valBuf.setLength(0);
            return TokenFactory.newString(val).at(location);
          }
        }
        case LEX_IN_NUMERIC_LITERAL -> {
          if (Character.isDigit(ch1)) {
            location.extendToken();
            valBuf.appendCodePoint(ch1);
          } else {
            codepoints.pushback(ch1);
            final String val = valBuf.toString();
            valBuf.setLength(0);
            return TokenFactory.newNumber(val).at(location);
          }
        }
        case LEX_IN_IDENTIFIER -> {
          if (Character.isJavaIdentifierPart(ch1)) {
            location.extendToken();
            valBuf.appendCodePoint(ch1);
          } else {
            codepoints.pushback(ch1);
            final String val = valBuf.toString().toUpperCase(Locale.ROOT);
            valBuf.setLength(0);
            return TokenFactory.newIdentifierOrKeyword(val).at(location);
          }
        }
      }
    }
  }

  @Override
  public void close() {
    codepoints.close();
  }
}
