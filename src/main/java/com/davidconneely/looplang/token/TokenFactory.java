package com.davidconneely.looplang.token;

import static com.davidconneely.looplang.token.Token.Kind.ASSIGN;
import static com.davidconneely.looplang.token.Token.Kind.COMMA;
import static com.davidconneely.looplang.token.Token.Kind.DO;
import static com.davidconneely.looplang.token.Token.Kind.END;
import static com.davidconneely.looplang.token.Token.Kind.EOF;
import static com.davidconneely.looplang.token.Token.Kind.IDENTIFIER;
import static com.davidconneely.looplang.token.Token.Kind.INPUT;
import static com.davidconneely.looplang.token.Token.Kind.LOOP;
import static com.davidconneely.looplang.token.Token.Kind.LPAREN;
import static com.davidconneely.looplang.token.Token.Kind.NUMBER;
import static com.davidconneely.looplang.token.Token.Kind.PLUS;
import static com.davidconneely.looplang.token.Token.Kind.PRINT;
import static com.davidconneely.looplang.token.Token.Kind.PROGRAM;
import static com.davidconneely.looplang.token.Token.Kind.RPAREN;
import static com.davidconneely.looplang.token.Token.Kind.SEMICOLON;
import static com.davidconneely.looplang.token.Token.Kind.STRING;

import java.util.Map;

public final class TokenFactory {
  private static final Map<String, Token.Kind> keywords;

  static {
    keywords =
        Map.of(
            "PROGRAM", PROGRAM, "LOOP", LOOP, "DO", DO, "END", END, "INPUT", INPUT, "PRINT", PRINT);
  }

  private TokenFactory() {}

  public static Token TOK_EOF = new SimpleToken(EOF, null);
  public static Token TOK_ASSIGN = new SimpleToken(ASSIGN, null);
  public static Token TOK_PLUS = new SimpleToken(PLUS, null);
  public static Token TOK_LPAREN = new SimpleToken(LPAREN, null);
  public static Token TOK_RPAREN = new SimpleToken(RPAREN, null);
  public static Token TOK_COMMA = new SimpleToken(COMMA, null);
  public static Token TOK_SEMICOLON = new SimpleToken(SEMICOLON, null);

  public static Token newString(final String string) {
    return new SimpleToken(STRING, string);
  }

  public static Token newNumber(final String number) {
    return new SimpleToken(NUMBER, number);
  }

  /** Identifier or keyword (based on value). */
  public static Token newIdentifierOrKeyword(final String value) {
    final Token.Kind kind = keywords.getOrDefault(value, IDENTIFIER);
    return new SimpleToken(kind, kind == IDENTIFIER ? value : null);
  }
}
