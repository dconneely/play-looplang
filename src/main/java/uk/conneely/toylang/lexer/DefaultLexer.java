package uk.conneely.toylang.lexer;

import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Locale;
import uk.conneely.toylang.token.Token;
import uk.conneely.toylang.token.TokenFactory;

final class DefaultLexer implements Lexer {
    enum State {
        INITIAL,
        WITHIN_CMMNT,
        WITHIN_IDENT,
        WITHIN_INTNUM,
        WITHIN_STRLIT
    }

    private final CharInput chars;
    private final Deque<Token> lookahead;

    DefaultLexer(final CharInput chars) {
        this.chars = chars;
        this.lookahead = new LinkedList<>();
    }

    @Override
    public void pushback(final Token token) {
        this.lookahead.push(token);
    }

    @Override
    public Token next() throws IOException {
        if (!this.lookahead.isEmpty()) {
            return this.lookahead.pop();
        }
        State state = State.INITIAL;
        final StringBuilder valBuf = new StringBuilder();
        while (true) {
            final int ch = this.chars.next();
            switch (state) {
                case INITIAL:
                    if (ch == '(') {
                        return TokenFactory.oparen();
                    } else if (ch == ')') {
                        return TokenFactory.cparen();
                    } else if (ch == ':' || ch == ';') {
                        return TokenFactory.stsep();
                    } else if (ch == '\r') {
                        final int ch2 = this.chars.next();
                        if (ch2 == '\n') {
                            return TokenFactory.lnsep();
                        }
                        this.chars.pushback(ch2);
                        return TokenFactory.lnsep();
                    } else if (ch == '\n') {
                        return TokenFactory.lnsep();
                    } else if (Character.isJavaIdentifierStart(ch)) {
                        state = State.WITHIN_IDENT;
                        valBuf.append((char) ch);
                    } else if (Character.isDigit(ch)) {
                        state = State.WITHIN_INTNUM;
                        valBuf.append((char) ch);
                    } else if (ch == '\"') {
                        state = State.WITHIN_STRLIT;
                    } else if (ch == '#') {
                        state = State.WITHIN_CMMNT;
                        valBuf.append((char) ch);
                    } else if (ch == -1) {
                        return TokenFactory.eof();
                    } else if (!Character.isWhitespace(ch)) {
                        throw new LexerException("unrecognized token: " + (char) ch);
                    }
                    // ignore whitespaces
                    break;
                case WITHIN_IDENT:
                    if (Character.isJavaIdentifierPart(ch)) {
                        valBuf.append((char) ch);
                    } else {
                        this.chars.pushback(ch);
                        final String val = valBuf.toString().toLowerCase(Locale.ROOT);
                        valBuf.setLength(0);
                        return TokenFactory.kwident(val);
                    }
                    break;
                case WITHIN_INTNUM:
                    if (Character.isDigit(ch)) {
                        valBuf.append((char) ch);
                    } else {
                        this.chars.pushback(ch);
                        final String val = valBuf.toString();
                        valBuf.setLength(0);
                        return TokenFactory.intnum(val);
                    }
                    break;
                case WITHIN_STRLIT:
                    if (ch == '\\') {
                        int ch2 = this.chars.next();
                        switch (ch2) {
                            case 'n':
                                valBuf.append(System.lineSeparator());
                                break;
                            case 't':
                                valBuf.append('\t');
                                break;
                            case '\n':
                            case '\r':
                                throw new LexerException("illegal character in string literal escape, char " + ch);
                            default:
                                valBuf.append((char) ch2);
                                break;
                        }
                    } else if (ch == '\n' || ch == '\r') {
                        throw new LexerException("illegal character in string literal, char " + ch);
                    } else if (ch != '\"') {
                        valBuf.append((char) ch);
                    } else {
                        final String val = valBuf.toString();
                        valBuf.setLength(0);
                        return TokenFactory.strlit(val);
                    }
                    break;
                case WITHIN_CMMNT:
                    if (ch != '\r' && ch != '\n') {
                        valBuf.append((char) ch);
                    } else {
                        this.chars.pushback(ch);
                        final String val = valBuf.toString();
                        valBuf.setLength(0);
                        return TokenFactory.cmmnt(val);
                    }
                    break;
            }
        }
    }
}
