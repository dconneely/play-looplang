package com.davidconneely.looplang.parser;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.davidconneely.looplang.ast.Node;
import com.davidconneely.looplang.ast.NodeFactory;
import com.davidconneely.looplang.lexer.Lexer;
import com.davidconneely.looplang.token.Token;

final class DefaultParser implements Parser {
    private final Lexer lexer;
    private final Token.Kind until;
    private final Set<String> programs;

    DefaultParser(final Lexer lexer) {
        this.lexer = lexer;
        this.until = Token.Kind.EOF;
        this.programs = new HashSet<>();
    }

    DefaultParser(final Lexer lexer, final Token.Kind until, Set<String> programs) {
        this.lexer = lexer;
        this.until = until;
        this.programs = programs;
    }

    @Override
    public Node next() throws IOException {
        while (true) {
            Node node;
            Token token = lexer.next();
            switch (token.kind()) {
                case IDENTIFIER:
                    // could be an AssignNumber, AssignPlus or AssignCall - takes up to 4 tokens (including `token`) to tell which :(.
                    Token assign = lexer.next();
                    if (assign.kind() == Token.Kind.ASSIGN) {
                        Token arg1 = lexer.next();
                        if (arg1.kind() == Token.Kind.NUMBER) {
                            node = NodeFactory.newAssignNumber();
                        } else if (arg1.kind() == Token.Kind.IDENTIFIER) {
                            Token arg2 = lexer.next();
                            if (arg2.kind() == Token.Kind.PLUS) {
                                node = NodeFactory.newAssignPlus();
                            } else if (arg2.kind() == Token.Kind.LPAREN) {
                                node = NodeFactory.newAssignCall(programs);
                            } else {
                                throw new ParserException("expected `+` or `(` after `" + token.textValue() + " := " + arg1.textValue() + "` at start of statement; got " + arg2);
                            }
                            lexer.pushback(arg2);
                        } else {
                            throw new ParserException("expected number or identifier after `" + token.textValue() + " :=` at start of statement; got " + arg1);
                        }
                        lexer.pushback(arg1);
                    } else {
                        throw new ParserException("expected `:=` after `" + token.textValue() + "` at start of statement; got " + assign);
                    }
                    lexer.pushback(assign);
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
                lexer.pushback(token);
                node.parse(lexer);
            }
            return node;
        }
    }
}
