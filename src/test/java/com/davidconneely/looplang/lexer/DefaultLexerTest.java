package com.davidconneely.looplang.lexer;

import com.davidconneely.looplang.token.Token;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

import static com.davidconneely.looplang.token.Token.Kind.*;
import static org.junit.jupiter.api.Assertions.*;

class DefaultLexerTest {

    private Lexer createLexer(String input) {
        return LexerFactory.newLexer(Location.newFile("<test>"), new StringReader(input));
    }

    @Test
    void emptyInput_returnsEof() throws IOException {
        Lexer lexer = createLexer("");
        assertEquals(EOF, lexer.next().kind());
    }

    @Test
    void whitespaceOnly_returnsEof() throws IOException {
        Lexer lexer = createLexer("   \t  \n  ");
        assertEquals(EOF, lexer.next().kind());
    }

    @Test
    void assignOperator_returnsAssignToken() throws IOException {
        Lexer lexer = createLexer(":=");
        Token token = lexer.next();
        assertEquals(ASSIGN, token.kind());
    }

    @Test
    void singleCharTokens_returnCorrectKinds() throws IOException {
        Lexer lexer = createLexer("+ ( ) , ;");
        assertEquals(PLUS, lexer.next().kind());
        assertEquals(LPAREN, lexer.next().kind());
        assertEquals(RPAREN, lexer.next().kind());
        assertEquals(COMMA, lexer.next().kind());
        assertEquals(SEMICOLON, lexer.next().kind());
        assertEquals(EOF, lexer.next().kind());
    }

    @Test
    void keywords_returnCorrectKinds() throws IOException {
        Lexer lexer = createLexer("PROGRAM LOOP DO END INPUT PRINT");
        assertEquals(PROGRAM, lexer.next().kind());
        assertEquals(LOOP, lexer.next().kind());
        assertEquals(DO, lexer.next().kind());
        assertEquals(END, lexer.next().kind());
        assertEquals(INPUT, lexer.next().kind());
        assertEquals(PRINT, lexer.next().kind());
    }

    @Test
    void keywords_areCaseInsensitive() throws IOException {
        Lexer lexer = createLexer("program Program PROGRAM PrOgRaM");
        assertEquals(PROGRAM, lexer.next().kind());
        assertEquals(PROGRAM, lexer.next().kind());
        assertEquals(PROGRAM, lexer.next().kind());
        assertEquals(PROGRAM, lexer.next().kind());
    }

    @Test
    void identifier_returnsIdentifierToken() throws IOException {
        Lexer lexer = createLexer("x0 myVar ABC");
        Token token1 = lexer.next();
        assertEquals(IDENTIFIER, token1.kind());
        assertEquals("X0", token1.value());

        Token token2 = lexer.next();
        assertEquals(IDENTIFIER, token2.kind());
        assertEquals("MYVAR", token2.value());

        Token token3 = lexer.next();
        assertEquals(IDENTIFIER, token3.kind());
        assertEquals("ABC", token3.value());
    }

    @Test
    void numericLiteral_returnsNumberToken() throws IOException {
        Lexer lexer = createLexer("0 42 12345");
        Token token1 = lexer.next();
        assertEquals(NUMBER, token1.kind());
        assertEquals(0, token1.valueInt());

        Token token2 = lexer.next();
        assertEquals(NUMBER, token2.kind());
        assertEquals(42, token2.valueInt());

        Token token3 = lexer.next();
        assertEquals(NUMBER, token3.kind());
        assertEquals(12345, token3.valueInt());
    }

    @Test
    void stringLiteral_returnsStringToken() throws IOException {
        Lexer lexer = createLexer("\"hello world\"");
        Token token = lexer.next();
        assertEquals(STRING, token.kind());
        assertEquals("hello world", token.value());
    }

    @Test
    void stringLiteral_handlesEscapeSequences() throws IOException {
        Lexer lexer = createLexer("\"line1\\nline2\\ttab\\\\backslash\\\"quote\"");
        Token token = lexer.next();
        assertEquals(STRING, token.kind());
        assertEquals("line1\nline2\ttab\\backslash\"quote", token.value());
    }

    @Test
    void comment_isIgnored() throws IOException {
        Lexer lexer = createLexer("x0 # this is a comment\nx1");
        Token token1 = lexer.next();
        assertEquals(IDENTIFIER, token1.kind());
        assertEquals("X0", token1.value());

        Token token2 = lexer.next();
        assertEquals(IDENTIFIER, token2.kind());
        assertEquals("X1", token2.value());
    }

    @Test
    void pushback_returnsTokenAgain() throws IOException {
        Lexer lexer = createLexer("x0 x1");
        Token token1 = lexer.next();
        lexer.pushback(token1);
        Token token2 = lexer.next();
        assertSame(token1, token2);
    }

    @Test
    void colonWithoutEquals_throwsException() {
        Lexer lexer = createLexer(": x");
        assertThrows(LexerException.class, lexer::next);
    }

    @Test
    void unterminatedString_throwsException() {
        Lexer lexer = createLexer("\"unterminated");
        assertThrows(LexerException.class, lexer::next);
    }

    @Test
    void unterminatedStringAtNewline_throwsException() {
        Lexer lexer = createLexer("\"unterminated\nx0");
        assertThrows(LexerException.class, lexer::next);
    }

    @Test
    void unrecognisedSymbol_throwsException() {
        Lexer lexer = createLexer("@");
        assertThrows(LexerException.class, lexer::next);
    }

    @Test
    void complexStatement_tokenisesCorrectly() throws IOException {
        Lexer lexer = createLexer("x0 := x1 + 1");
        assertEquals(IDENTIFIER, lexer.next().kind());
        assertEquals(ASSIGN, lexer.next().kind());
        assertEquals(IDENTIFIER, lexer.next().kind());
        assertEquals(PLUS, lexer.next().kind());
        assertEquals(NUMBER, lexer.next().kind());
        assertEquals(EOF, lexer.next().kind());
    }
}
