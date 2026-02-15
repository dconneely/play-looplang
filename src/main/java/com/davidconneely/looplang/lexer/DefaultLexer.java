package com.davidconneely.looplang.lexer;

import com.davidconneely.looplang.token.Token;
import com.davidconneely.looplang.token.TokenFactory;

import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Locale;

import static com.davidconneely.looplang.token.TokenFactory.*;

final class DefaultLexer implements Lexer {
    private enum LexState {
        LEX_INITIAL,
        LEX_IN_COMMENT_LINE,
        LEX_IN_STRING_LITERAL,
        LEX_IN_NUMERIC_LITERAL,
        LEX_IN_IDENTIFIER
    }

    private final Location location;
    private final CharInput chars;
    private final Deque<Token> lookahead;

    DefaultLexer(final Location location, final CharInput chars) {
        this.location = location;
        this.chars = chars;
        this.lookahead = new LinkedList<>();
    }

    @Override
    public void pushback(final Token token) {
        lookahead.push(token);
    }

    @Override
    public Token next() throws IOException {
        if (!lookahead.isEmpty()) {
            return lookahead.pop();
        }
        LexState state = LexState.LEX_INITIAL;
        final StringBuilder valBuf = new StringBuilder();
        while (true) {
            final int ch1 = chars.next();
            int ch2;
            switch (state) {
                case LEX_INITIAL -> {
                    location.nextToken();
                    switch (ch1) {
                        case -1 -> { return TOK_EOF; }
                        case '\r' -> {
                            ch2 = chars.next();
                            if (ch2 != '\n') {
                                chars.pushback(ch2);
                                throw new LexerException("standalone `CR` control character (not followed by `LF`)", location);
                            }
                            location.nextLine();
                        }
                        case '\n' -> location.nextLine();
                        case ':' -> {
                            ch2 = chars.next();
                            if (ch2 != '=') {
                                chars.pushback(ch2);
                                throw new LexerException("colon not followed by `=`, but `" + (char) ch2 + "` (" + ch2 + ")", location);
                            }
                            location.extendToken();
                            return TOK_ASSIGN.at(location);
                        }
                        case '+' -> { return TOK_PLUS.at(location); }
                        case '(' -> { return TOK_LPAREN.at(location); }
                        case ')' -> { return TOK_RPAREN.at(location); }
                        case ',' -> { return TOK_COMMA.at(location); }
                        case ';' -> { return TOK_SEMICOLON.at(location); }
                        case '#' -> state = LexState.LEX_IN_COMMENT_LINE;
                        case '\"' -> state = LexState.LEX_IN_STRING_LITERAL;
                        default -> {
                            if (ch1 >= '0' && ch1 <= '9') { // could have used Character.isDigit
                                state = LexState.LEX_IN_NUMERIC_LITERAL;
                                valBuf.append((char) ch1);
                            } else if ((ch1 >= 'A' && ch1 <= 'Z') || (ch1 >= 'a' && ch1 <= 'z')) { // could have used Character.isJavaIdentifierStart
                                state = LexState.LEX_IN_IDENTIFIER;
                                valBuf.append((char) ch1);
                            } else if (ch1 != ' ' && ch1 != '\t') { // could have used Character.isWhitespace
                                throw new LexerException("unrecognized symbol `" + (char) ch1 + "` (" + ch1 + ")", location);
                            }
                            // ignore whitespace (' ' or '\t') when in LEX_INITIAL state.
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
                    if (ch1 == -1) {
                        throw new LexerException("unterminated string literal at end of file", location);
                    } else if (ch1 == '\\') {
                        ch2 = chars.next();
                        location.extendToken();
                        switch (ch2) {
                            case 't' -> valBuf.append('\t');
                            case 'n' -> valBuf.append('\n');
                            case 'r' -> valBuf.append('\r');
                            case '"', '\\' -> valBuf.append((char) ch2);
                            default -> throw new LexerException("unexpected string escape character (#" + ch2 + ")", location);
                        }
                    } else if (ch1 == '\n') {
                        Location endOfLine = Location.copyOf(location);
                        location.nextLine();
                        throw new LexerException("missing closing quote on string literal", endOfLine);
                    } else if (ch1 != '"') {
                        valBuf.append((char) ch1);
                    } else {
                        // we want to consume the '"' as part of the string, so no pushback.
                        final String val = valBuf.toString();
                        valBuf.setLength(0);
                        return TokenFactory.newString(val).at(location);
                    }
                }
                case LEX_IN_NUMERIC_LITERAL -> {
                    if (ch1 >= '0' && ch1 <= '9') { // could have used Character.isDigit
                        location.extendToken();
                        valBuf.append((char) ch1);
                    } else {
                        chars.pushback(ch1);
                        final String val = valBuf.toString();
                        valBuf.setLength(0);
                        return TokenFactory.newNumber(val).at(location);
                    }
                }
                case LEX_IN_IDENTIFIER -> {
                    if ((ch1 >= 'A' && ch1 <= 'Z') || (ch1 >= 'a' && ch1 <= 'z')
                            || (ch1 >= '0' && ch1 <= '9') || ch1 == '_') { // could have used Character.isJavaIdentifierPart
                        location.extendToken();
                        valBuf.append((char) ch1);
                    } else {
                        chars.pushback(ch1);
                        final String val = valBuf.toString().toUpperCase(Locale.ROOT);
                        valBuf.setLength(0);
                        return TokenFactory.newIdentifierOrKeyword(val).at(location);
                    }
                }
            }
        }
    }
}
