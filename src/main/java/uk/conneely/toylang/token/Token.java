package uk.conneely.toylang.token;

public interface Token {
    enum Kind {
        EOF,       // end-of-file
        LNSEP,     // end-of-line
        CMMNT,     // '#'
        STSEP,     // ';'
        IDENT,     // identifier
        INTNUM,    // non-negative integer numeric literal
        STRLIT,    // quoted string literal
        OPAREN,    // '('
        CPAREN,    // ')'
        KWDEF,     // 'def'
        KWFOR,     // 'for'
        KWINC,     // 'inc'
        KWPRINTLN, // 'println'
        KWSTZ      // 'stz'
    }

    Kind kind();

    String textValue();

    int intValue();

    boolean endsStmt();
}
