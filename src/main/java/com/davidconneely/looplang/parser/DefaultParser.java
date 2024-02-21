package com.davidconneely.looplang.parser;

import com.davidconneely.looplang.ast.Node;
import com.davidconneely.looplang.ast.NodeFactory;
import com.davidconneely.looplang.lexer.Lexer;
import com.davidconneely.looplang.token.Token;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static com.davidconneely.looplang.ast.NodeUtils.throwUnexpectedParserException;
import static com.davidconneely.looplang.token.Token.Kind.*;

final class DefaultParser implements Parser {
    private final Lexer lexer;
    private final Token.Kind until;
    private final Set<String> programs;

    DefaultParser(final Lexer lexer) {
        this.lexer = lexer;
        this.until = Token.Kind.EOF;
        this.programs = new HashSet<>();
    }

    DefaultParser(final Lexer lexer, final Token.Kind until, final Set<String> programs) {
        this.lexer = lexer;
        this.until = until;
        this.programs = programs;
    }

    @Override
    public Node next() throws IOException {
        while (true) {
            Node node = null;
            Token token = lexer.next();
            switch (token.kind()) {
                case IDENTIFIER:
                    node = nextAssign();
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
                case SEMICOLON, COMMENT, NEWLINE:
                    continue; // in while loop, so ignore
                default:
                    if (token.kind() == until) {
                        return null;
                    }
                    throwUnexpectedParserException(until, "or a new statement", token);
            }
            lexer.pushback(token);
            node.parse(lexer);
            return node;
        }
    }

    // could be an AssignNumber, AssignPlus, AssignInput or AssignCall - takes up to 4 tokens to tell which :(.
    private Node nextAssign() throws IOException {
        Token assign = lexer.next();
        if (assign.kind() != ASSIGN) {
            throwUnexpectedParserException(ASSIGN, "after lvalue in assignment", assign);
        }
        Token arg1 = lexer.next();
        if (arg1.kind() == NUMBER) {
            lexer.pushback(arg1); lexer.pushback(assign);
            return NodeFactory.newAssignNumber();
        } else if (arg1.kind() == KW_INPUT) {
            lexer.pushback(arg1); lexer.pushback(assign);
            return NodeFactory.newAssignInput();
        } else if (arg1.kind() != IDENTIFIER) {
            throwUnexpectedParserException(NUMBER, KW_INPUT, IDENTIFIER, "after `:=` in assignment", arg1);
        }
        Token arg2 = lexer.next();
        if (arg2.kind() == PLUS) {
            lexer.pushback(arg2); lexer.pushback(arg1); lexer.pushback(assign);
            return NodeFactory.newAssignPlus();
        } else if (arg2.kind() == LPAREN) {
            lexer.pushback(arg2); lexer.pushback(arg1); lexer.pushback(assign);
            return NodeFactory.newAssignCall(programs);
        }
        throwUnexpectedParserException(PLUS, LPAREN, "after rvalue identifier in assignment", arg2);
        return null;  // never reached
    }
}
