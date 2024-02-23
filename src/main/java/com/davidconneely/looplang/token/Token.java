package com.davidconneely.looplang.token;

import com.davidconneely.looplang.lexer.Location;

public interface Token {
    enum Kind {
        EOF,         // the end of the file input
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

    String value();

    int valueInt();

    default Token at(Location location) {
        return new LocatedToken(this, Location.copyOf(location));
    }

    static String escaped(String string) {
        return '"' + string.replace("\\", "\\\\")
                .replace("\t", "\\t")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\"", "\\\"") + '"';
    }
}
