package com.davidconneely.looplang.lexer;

import com.davidconneely.looplang.token.Token;
import com.davidconneely.looplang.token.TokenFactory;

import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Locale;

import static com.davidconneely.looplang.token.TokenFactory.*;

final class DefaultLexer implements Lexer {
    private enum State {
        LEX_INITIAL,
        LEX_IN_COMMENT,
        LEX_IN_STRING,
        LEX_IN_NUMBER,
        LEX_IN_IDENTIFIER
    }

    private final CharInput chars;
    private final Deque<Token> lookahead;

    DefaultLexer(final CharInput chars) {
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
        State state = State.LEX_INITIAL;
        final StringBuilder valBuf = new StringBuilder();
        while (true) {
            final int ch1 = chars.next();
            int ch2;
            switch (state) {
                case LEX_INITIAL:
                    switch (ch1) {
                        case -1:
                            return TOK_EOF;
                        case '\r':
                            ch2 = chars.next();
                            if (ch2 != '\n') {
                                chars.pushback(ch2);
                                throw new LexerException("standalone `CR` control character (not followed by `LF`)");
                            }
                            return TOK_NEWLINE;
                        case '\n':
                            return TOK_NEWLINE;
                        case ':':
                            ch2 = chars.next();
                            if (ch2 != '=') {
                                chars.pushback(ch2);
                                throw new LexerException("colon not followed by `=`, but `" + (char) ch2 + "` (" + ch2 + ")");
                            }
                            return TOK_ASSIGN;
                        case '+':
                            return TOK_PLUS;
                        case '(':
                            return TOK_LPAREN;
                        case ')':
                            return TOK_RPAREN;
                        case ',':
                            return TOK_COMMA;
                        case ';':
                            return TOK_SEMICOLON;
                        case '#':
                            state = State.LEX_IN_COMMENT;
                            valBuf.append('#');
                            break;
                        case '\"':
                            state = State.LEX_IN_STRING;
                            break;
                        default:
                            if (ch1 >= '0' && ch1 <= '9') { // could have used Character.isDigit
                                state = State.LEX_IN_NUMBER;
                                valBuf.append((char) ch1);
                            } else if ((ch1 >= 'A' && ch1 <= 'Z') || (ch1 >= 'a' && ch1 <= 'z')) { // could have used Character.isJavaIdentifierStart
                                state = State.LEX_IN_IDENTIFIER;
                                valBuf.append((char) ch1);
                            } else if (ch1 != ' ' && ch1 != '\t') { // could have used Character.isWhitespace
                                throw new LexerException("unrecognized symbol `" + (char) ch1 + "` (" + ch1 + ")");
                            }
                            break; // ignore whitespace (' ' or '\t') when in LEX_INITIAL state.
                    }
                    break;
                case LEX_IN_COMMENT:
                    if (ch1 != '\r' && ch1 != '\n') {
                        valBuf.append((char) ch1);
                    } else {
                        // no need to pushback CR or LF.
                        final String val = valBuf.toString();
                        valBuf.setLength(0);
                        return TokenFactory.newComment(val);
                    }
                    break;
                case LEX_IN_STRING:
                    if (ch1 == '\\') {
                        ch2 = chars.next();
                        switch (ch2) {
                            case 't':
                                valBuf.append('\t');
                                break;
                            case 'n':
                                valBuf.append('\n');
                                break;
                            case 'r':
                                valBuf.append('\r');
                                break;
                            case '"', '\\':
                                valBuf.append((char) ch2);
                                break;
                            default:
                                throw new LexerException("unexpected string escape character (" + ch2 + ")");
                        }
                    } else if (ch1 == '\n' || ch1 == '\r') {
                        throw new LexerException("unexpected `CR` or `LF` in string literal (" + ch1 + ")");
                    } else if (ch1 != '"') {
                        valBuf.append((char) ch1);
                    } else {
                        // we want to consume the '"' as part of the string, so no pushback.
                        final String val = valBuf.toString();
                        valBuf.setLength(0);
                        return TokenFactory.newString(val);
                    }
                    break;
                case LEX_IN_NUMBER:
                    if (ch1 >= '0' && ch1 <= '9') { // could have used Character.isDigit
                        valBuf.append((char) ch1);
                    } else {
                        chars.pushback(ch1);
                        final String val = valBuf.toString();
                        valBuf.setLength(0);
                        return TokenFactory.newNumber(val);
                    }
                    break;
                case LEX_IN_IDENTIFIER:
                     if ((ch1 >= 'A' && ch1 <= 'Z') || (ch1 >= 'a' && ch1 <= 'z')
                            || (ch1 >= '0' && ch1 <= '9') || ch1 == '_') { // could have used Character.isJavaIdentifierPart
                        valBuf.append((char) ch1);
                    } else {
                        chars.pushback(ch1);
                        final String val = valBuf.toString().toUpperCase(Locale.ROOT);
                        valBuf.setLength(0);
                        return TokenFactory.newIdentifierOrKeyword(val);
                    }
                    break;
            }
        }
    }
}
