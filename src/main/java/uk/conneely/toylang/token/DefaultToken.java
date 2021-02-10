package uk.conneely.toylang.token;

import uk.conneely.toylang.lexer.LexerException;

final class DefaultToken implements Token {
    private final Kind kind;
    private final String value;

    DefaultToken(final Kind kind, final String value) {
        this.kind = kind;
        this.value = value;
    }

    @Override
    public Kind kind() {
        return this.kind;
    }

    @Override
    public String textValue() {
        return this.value;
    }

    @Override
    public int intValue() {
        if (this.kind != Kind.INTNUM) {
            throw new LexerException("not a numeric literal kind - " + this);
        }
        try {
            return Integer.parseInt(this.value);
        } catch (NumberFormatException e) {
            throw new LexerException("not a numeric literal value - " + this);
        }
    }

    @Override
    public boolean endsStmt() {
        return kind == Kind.EOF || kind == Kind.LNSEP || kind == Kind.CMMNT ||
                kind == Kind.STSEP || kind == Kind.CPAREN;
    }

    @Override
    public String toString() {
        return kind + ":`" + value + "`";
    }
}
