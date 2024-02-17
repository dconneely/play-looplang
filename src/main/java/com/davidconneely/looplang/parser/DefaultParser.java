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
    private final Set<String> programs;

    DefaultParser(final Lexer tokens) {
        this.tokens = tokens;
        this.until = Token.Kind.EOF;
        this.programs = new HashSet<>();
    }

    DefaultParser(final Lexer tokens, final Token.Kind until, Set<String> programs) {
        this.tokens = tokens;
        this.until = until;
        this.programs = programs;
    }

    @Override
    public Node next() throws IOException {
        while (true) {
            Node node;
            Token token = tokens.next();
            switch (token.kind()) {
                case IDENTIFIER:
                    node = NodeFactory.newAssignment(programs);
                    break;
                case KW_INPUT:
                    node = NodeFactory.newInput();
                    break;
                case KW_PRINT:
                    node = NodeFactory.newPrint();
                    break;
                case KW_LOOP:
                    node = NodeFactory.newLoop(programs);
                    break;
                case KW_PROGRAM:
                    node = NodeFactory.newDefinition(programs);
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
