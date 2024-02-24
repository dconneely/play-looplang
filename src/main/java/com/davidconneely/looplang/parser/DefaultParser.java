package com.davidconneely.looplang.parser;

import com.davidconneely.looplang.lexer.Lexer;
import com.davidconneely.looplang.statement.Statement;
import com.davidconneely.looplang.statement.StatementFactory;
import com.davidconneely.looplang.token.Token;

import java.io.IOException;

import static com.davidconneely.looplang.statement.StatementUtils.throwUnexpectedParserException;
import static com.davidconneely.looplang.token.Token.Kind.*;

final class DefaultParser implements Parser {
    private final Lexer lexer;
    private final ParserContext context;
    private final Token.Kind until;

    DefaultParser(final Lexer lexer, final ParserContext context, final Token.Kind until) {
        this.lexer = lexer;
        this.context = context;
        this.until = until;
    }

    @Override
    public Statement next() throws IOException {
        while (true) {
            Token token = lexer.next();
            switch (token.kind()) {
                case IDENTIFIER:
                    return nextAssign(token);
                case KW_PRINT:
                    lexer.pushback(token);
                    return StatementFactory.newPrint(context, lexer);
                case KW_LOOP:
                    lexer.pushback(token);
                    return StatementFactory.newLoop(context, lexer);
                case KW_PROGRAM:
                    lexer.pushback(token);
                    return StatementFactory.newDefinition(context, lexer);
                case SEMICOLON:
                    continue; // in while loop, so ignore
                default:
                    if (token.kind() == until) {
                        return null;
                    }
                    throwUnexpectedParserException(until, "or a new statement", token);
            }
        }
    }

    // could be an AssignNumber, AssignPlus, AssignInput or AssignCall - takes up to 4 tokens to tell which :(.
    private Statement nextAssign(final Token identifier) throws IOException {
        Token assign = lexer.next();
        if (assign.kind() != ASSIGN) {
            throwUnexpectedParserException(ASSIGN, "after lvalue in assignment", assign);
        }
        Token arg1 = lexer.next();
        if (arg1.kind() == NUMBER) {
            lexer.pushback(arg1); lexer.pushback(assign); lexer.pushback(identifier);
            return StatementFactory.newAssignNumber(context, lexer);
        } else if (arg1.kind() == KW_INPUT) {
            lexer.pushback(arg1); lexer.pushback(assign); lexer.pushback(identifier);
            return StatementFactory.newAssignInput(context, lexer);
        } else if (arg1.kind() != IDENTIFIER) {
            throwUnexpectedParserException(NUMBER, KW_INPUT, IDENTIFIER, "after `:=` in assignment", arg1);
        }
        Token arg2 = lexer.next();
        if (arg2.kind() == PLUS) {
            lexer.pushback(arg2); lexer.pushback(arg1); lexer.pushback(assign); lexer.pushback(identifier);
            return StatementFactory.newAssignPlus(context, lexer);
        } else if (arg2.kind() == LPAREN) {
            lexer.pushback(arg2); lexer.pushback(arg1); lexer.pushback(assign); lexer.pushback(identifier);
            return StatementFactory.newAssignCall(context, lexer);
        }
        throwUnexpectedParserException(PLUS, LPAREN, "after rvalue identifier in assignment", arg2);
        return null;  // never reached
    }
}
