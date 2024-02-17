package com.davidconneely.looplang.ast;

import java.io.IOException;
import java.util.*;

import com.davidconneely.looplang.interpreter.Context;
import com.davidconneely.looplang.interpreter.Interpreter;
import com.davidconneely.looplang.interpreter.InterpreterException;
import com.davidconneely.looplang.interpreter.InterpreterFactory;
import com.davidconneely.looplang.lexer.Lexer;
import com.davidconneely.looplang.parser.ParserException;
import com.davidconneely.looplang.token.Token;

final class AssignmentNode implements Node {
    enum Mode { NUMBER, PLUS, PROGRAM }

    private Mode mode;
    private String variable; // variable name on left of `:=` sign
    private int number; // for Mode.NUMBER, Mode.PLUS
    private String program; // for Mode.PROGRAM
    private List<String> args; // for Mode.PROGRAM
    private final Set<String> programs;

    AssignmentNode(final Set<String> programs) {
        this.programs = programs;
    }

    @Override
    public void parse(final Lexer lexer) throws IOException {
        Token token = lexer.next();
        if (token.kind() != Token.Kind.IDENTIFIER) {
            throw new ParserException("assignment: expected identifier (variable name); got " + token);
        }
        variable = token.textValue();
        token = lexer.next();
        if (token.kind() != Token.Kind.ASSIGN) {
            throw new ParserException("assignment: expected `:=`; got " + token);
        }
        token = lexer.next();
        if (token.kind() == Token.Kind.NUMBER) {
            mode = Mode.NUMBER;
            number = token.intValue();
            if (number != 0) {
                throw new ParserException("assignment: expected `0` as number after `:=`; got " + token);
            }
        } else if (token.kind() == Token.Kind.IDENTIFIER) {
            // could be Mode.PLUS or Mode.PROGRAM
            String identifier = token.textValue();
            token = lexer.next();
            if (token.kind() == Token.Kind.PLUS) {
                mode = Mode.PLUS;
                if (!variable.equals(identifier)) {
                    throw new ParserException("assignment: expected same identifier (variable name) in lvalue (`" + variable.toLowerCase(Locale.ROOT) + "`) and rvalue (`" + identifier.toLowerCase(Locale.ROOT) + "`) of addition assignment");
                }
                token = lexer.next();
                if (token.kind() != Token.Kind.NUMBER) {
                    throw new ParserException("assignment: expected number after '+' in addition assignment; got " + token);
                }
                number = token.intValue();
                if (number != 1) {
                    throw new ParserException("assignment: expected `1` as number after `+`; got " + token);
                }
            } else if (token.kind() == Token.Kind.LPAREN) {
                mode = Mode.PROGRAM;
                program = identifier;
                if (!programs.contains(program)) {
                    // necessary to prevent recursive calls
                    throw new ParserException("assignment: disallowed call to program `" + program + "`, that has not been fully-defined");
                }
                token = lexer.next();
                if (token.kind() != Token.Kind.IDENTIFIER && token.kind() != Token.Kind.RPAREN) {
                    throw new ParserException("assignment: expected identifier (variable name) or ')' in program call args; got " + token);
                }
                args = new ArrayList<>();
                while (token.kind() != Token.Kind.RPAREN) {
                    args.add(token.textValue());
                    token = lexer.next();
                    if (token.kind() == Token.Kind.COMMA) {
                        token = lexer.next();
                        if (token.kind() != Token.Kind.IDENTIFIER) {
                            throw new ParserException("assignment: expected identifier (variable name) in program call args; got " + token);
                        }
                    } else if (token.kind() != Token.Kind.RPAREN) {
                        throw new ParserException("assignment: expected ',' or ')' in program call args; got " + token);
                    }
                }
            } else {
                throw new ParserException("assignment: expected `+` or `(` after identifier name in assignment rvalue; got " + token);
            }
        } else {
            throw new ParserException("assignment: expected number or identifier after `:=`; got " + token);
        }
    }

    @Override
    public void interpret(final Context context) {
        switch (mode) {
            case NUMBER:
                context.setVariable(variable, number);
                break;
            case PLUS:
                context.setVariable(variable, context.getVariable(variable) + number);
                break;
            case PROGRAM:
                Context subcontext = context.getProgramContext(program, args);
                try {
                    subcontext.getVariable("X0");
                } catch (InterpreterException e) {
                    subcontext.setVariable("X0", 0);
                }
                final List<Node> body = context.getProgramBody(program);
                final Interpreter interpreter = InterpreterFactory.newInterpreter(subcontext);
                for (Node node : body) {
                    interpreter.interpret(node);
                }
                final int x0 = subcontext.getVariable("X0");
                context.setVariable(variable, x0);
                break;
        }
    }

    @Override
    public String toString() {
        if (mode == null || variable == null) {
            return "<uninitialized assignment>";
        }
        StringBuilder sb = new StringBuilder();
        switch (mode) {
            case NUMBER:
                sb.append(variable.toLowerCase(Locale.ROOT));
                sb.append(" := ");
                sb.append(number);
                break;
            case PLUS:
                sb.append(variable.toLowerCase(Locale.ROOT));
                sb.append(" := ");
                sb.append(variable.toLowerCase(Locale.ROOT));
                sb.append(" + ");
                sb.append(number);
                break;
            case PROGRAM:
                sb.append(variable.toLowerCase(Locale.ROOT));
                sb.append(" := ");
                sb.append(program);
                sb.append('(');
                if (args != null) {
                    boolean first = true;
                    for (String arg : args) {
                        if (first) { first = false; } else { sb.append(", "); }
                        sb.append(arg.toLowerCase(Locale.ROOT));
                    }
                }
                sb.append(')');
                break;
        }
        return sb.toString();
    }
}
