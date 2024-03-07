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
        PROGRAM,     // keyword `PROGRAM`
        LOOP,        // keyword `LOOP`
        DO,          // keyword `DO`
        END,         // keyword `END`
        INPUT,       // keyword `INPUT`
        PRINT        // keyword `PRINT`
    }

    Kind kind();

    String value();

    int valueInt();

    default Token at(final Location location) {
        return new LocatedToken(this, Location.copyOf(location));
    }

    static String escaped(final String string) {
        return '"' + string.replace("\\", "\\\\")
                .replace("\t", "\\t")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\"", "\\\"") + '"';
    }
}
