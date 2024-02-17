package com.davidconneely.looplang.lexer;

import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Locale;
import com.davidconneely.looplang.token.Token;
import com.davidconneely.looplang.token.TokenFactory;

final class DefaultLexer implements Lexer {
    enum State {
        INITIAL,
        IN_COMMENT,
        IN_STRING,
        IN_NUMBER,
        IN_IDENTIFIER
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
        State state = State.INITIAL;
        final StringBuilder valBuf = new StringBuilder();
        while (true) {
            final int ch1 = chars.next();
            int ch2;
            switch (state) {
                case INITIAL:
                    switch (ch1) {
                        case -1:
                            return TokenFactory.EOF;
                        case '\r':
                            ch2 = chars.next();
                            if (ch2 != '\n') {
                                chars.pushback(ch2);
                                throw new LexerException("standalone `CR` control character (not followed by `LF`)");
                            }
                            return TokenFactory.NEWLINE;
                        case '\n':
                            return TokenFactory.NEWLINE;
                        case ':':
                            ch2 = chars.next();
                            if (ch2 != '=') {
                                chars.pushback(ch2);
                                throw new LexerException("colon not followed by `=`, but `" + (char) ch2 + "` (" + ch2 + ")");
                            }
                            return TokenFactory.ASSIGN;
                        case '+':
                            return TokenFactory.PLUS;
                        case '(':
                            return TokenFactory.LPAREN;
                        case ')':
                            return TokenFactory.RPAREN;
                        case ',':
                            return TokenFactory.COMMA;
                        case ';':
                            return TokenFactory.SEMICOLON;
                        case '#':
                            state = State.IN_COMMENT;
                            valBuf.append('#');
                            break;
                        case '\"':
                            state = State.IN_STRING;
                            break;
                        default:
                            if (ch1 >= '0' && ch1 <= '9') {
                                state = State.IN_NUMBER;
                                valBuf.append((char) ch1);
                            } else if ((ch1 >= 'A' && ch1 <= 'Z') || (ch1 >= 'a' && ch1 <= 'z')) {
                                state = State.IN_IDENTIFIER;
                                valBuf.append((char) ch1);
                            } else if (ch1 != ' ' && ch1 != '\t') {
                                throw new LexerException("unrecognized symbol `" + (char) ch1 + "` (" + ch1 + ")");
                            }
                            // ignore whitespace (' ' or '\t')
                            break;
                    }
                    break;
                case IN_COMMENT:
                    if (ch1 != '\r' && ch1 != '\n') {
                        valBuf.append((char) ch1);
                    } else {
                        chars.pushback(ch1);
                        final String val = valBuf.toString();
                        valBuf.setLength(0);
                        return TokenFactory.newComment(val);
                    }
                    break;
                case IN_STRING:
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
                        final String val = valBuf.toString();
                        valBuf.setLength(0);
                        return TokenFactory.newString(val);
                    }
                    break;
                case IN_NUMBER:
                    if (ch1 >= '0' && ch1 <= '9') {
                        valBuf.append((char) ch1);
                    } else {
                        chars.pushback(ch1);
                        final String val = valBuf.toString();
                        valBuf.setLength(0);
                        return TokenFactory.newNumber(val);
                    }
                    break;
                case IN_IDENTIFIER:
                    if ((ch1 >= 'A' && ch1 <= 'Z') || (ch1 >= 'a' && ch1 <= 'z')
                            || (ch1 >= '0' && ch1 <= '9') || ch1 == '_') {
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
