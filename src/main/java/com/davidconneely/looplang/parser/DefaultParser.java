package com.davidconneely.looplang.parser;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.davidconneely.looplang.ast.Node;
import com.davidconneely.looplang.ast.NodeFactory;
import com.davidconneely.looplang.lexer.Lexer;
import com.davidconneely.looplang.token.Token;

final class DefaultParser implements Parser {
    private final Lexer tokens;
    private final Token.Kind until;
    private Set<String> definedPrograms;

    DefaultParser(final Lexer tokens) {
        this.tokens = tokens;
        this.until = Token.Kind.EOF;
        this.definedPrograms = new HashSet<>();
    }

    DefaultParser(final Lexer tokens, final Token.Kind until, Set<String> definedPrograms) {
        this.tokens = tokens;
        this.until = until;
        this.definedPrograms = definedPrograms;
    }

    @Override
    public Node next() throws IOException {
        while (true) {
            Node node;
            Token token = tokens.next();
            switch (token.kind()) {
                case IDENTIFIER:
                    node = NodeFactory.assignment(definedPrograms);
                    break;
                case KW_INPUT:
                    node = NodeFactory.input();
                    break;
                case KW_PRINT:
                    node = NodeFactory.print();
                    break;
                case KW_LOOP:
                    node = NodeFactory.loop(definedPrograms);
                    break;
                case KW_PROGRAM:
                    node = NodeFactory.programDefn(definedPrograms);
                    break;
                case SEMICOLON:
                case COMMENT:
                case NEWLINE:
                    continue;
                default:
                    if (token.kind() == until) {
                        node = null;
                        break;
                    }
                    throw new ParserException("unexpected token at start of statement, expected " + until + "; got " + token);
            }
            if (node != null) {
                tokens.pushback(token);
                node.parse(tokens);
            }
            return node;
        }
    }
}
