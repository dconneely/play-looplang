package com.davidconneely.looplang.token;

public interface Token {
    enum Kind {
        EOF,         // the end of the file input
        NEWLINE,     // a line separator (`\n` or `\r\n`)
        COMMENT,     // a line comment (`# ...`)
        ASSIGN,      // assignment operation (`:=`)
        PLUS,        // addition sign (`+`)
        LPAREN,      // left/open parenthesis (`(`)
        RPAREN,      // right/close parenthesis (`)`)
        COMMA,       // parameter separator (`,`)
        SEMICOLON,   // a statement separator (`;`)
        STRING,      // quoted string literal
        NUMBER,      // non-negative integer numeric literal
        IDENTIFIER,  // an identifier
        KW_PROGRAM,  // keyword `PROGRAM`
        KW_LOOP,     // keyword `LOOP`
        KW_DO,       // keyword `DO`
        KW_END,      // keyword `END`
        KW_INPUT,    // keyword `INPUT`
        KW_PRINT     // keyword `PRINT`
    }

    Kind kind();

    String textValue();

    int intValue();

    boolean endsStmt();
}
