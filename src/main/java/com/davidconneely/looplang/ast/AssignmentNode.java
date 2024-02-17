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
    private String lvalue; // variable name on left of `:=` sign
    private int number; // for Mode.NUMBER, Mode.PLUS
    private String program; // for Mode.PROGRAM
    private List<String> params; // for Mode.PROGRAM
    private Set<String> definedPrograms;

    AssignmentNode(Set<String> definedPrograms) {
        this.definedPrograms = definedPrograms;
    }

    @Override
    public void parse(final Lexer tokens) throws IOException {
        Token token = tokens.next();
        if (token.kind() != Token.Kind.IDENTIFIER) {
            throw new ParserException("assignment: expected identifier (variable name); got " + token);
        }
        lvalue = token.textValue();
        token = tokens.next();
        if (token.kind() != Token.Kind.ASSIGN) {
            throw new ParserException("assignment: expected `:=`; got " + token);
        }
        token = tokens.next();
        if (token.kind() == Token.Kind.NUMBER) {
            mode = Mode.NUMBER;
            number = token.intValue();
            if (number != 0) {
                throw new ParserException("assignment: expected `0` as number after `:=`; got " + token);
            }
        } else if (token.kind() == Token.Kind.IDENTIFIER) {
            // could be Mode.PLUS or Mode.PROGRAM
            String identifier = token.textValue();
            token = tokens.next();
            if (token.kind() == Token.Kind.PLUS) {
                mode = Mode.PLUS;
                if (!lvalue.equals(identifier)) {
                    throw new ParserException("assignment: expected same identifier (variable name) in lvalue (`" + lvalue.toLowerCase(Locale.ROOT) + "`) and rvalue (`" + identifier.toLowerCase(Locale.ROOT) + "`) of addition assignment");
                }
                token = tokens.next();
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
                if (!definedPrograms.contains(program)) {
                    // necessary to prevent recursive calls
                    throw new ParserException("assignment: disallowed call to program `" + program + "`, that has not been fully-defined");
                }
                token = tokens.next();
                if (token.kind() != Token.Kind.IDENTIFIER && token.kind() != Token.Kind.RPAREN) {
                    throw new ParserException("assignment: expected identifier (variable name) or ')' in program call params; got " + token);
                }
                params = new ArrayList<>();
                while (token.kind() != Token.Kind.RPAREN) {
                    params.add(token.textValue());
                    token = tokens.next();
                    if (token.kind() == Token.Kind.COMMA) {
                        token = tokens.next();
                        if (token.kind() != Token.Kind.IDENTIFIER) {
                            throw new ParserException("assignment: expected identifier (variable name) in program call params; got " + token);
                        }
                    } else if (token.kind() != Token.Kind.RPAREN) {
                        throw new ParserException("assignment: expected ',' or ')' in program call params; got " + token);
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
                context.setVariable(lvalue, number);
                break;
            case PLUS:
                context.setVariable(lvalue, context.getVariable(lvalue) + number);
                break;
            case PROGRAM:
                Context subcontext = context.getProgramContext(program, params);
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
                context.setVariable(lvalue, x0);
                break;
        }
    }

    @Override
    public String toString() {
        if (mode == null || lvalue == null) {
            return "<uninitialized assignment>";
        }
        StringBuilder sb = new StringBuilder();
        switch (mode) {
            case NUMBER:
                sb.append(lvalue.toLowerCase(Locale.ROOT));
                sb.append(" := ");
                sb.append(number);
                break;
            case PLUS:
                sb.append(lvalue.toLowerCase(Locale.ROOT));
                sb.append(" := ");
                sb.append(lvalue.toLowerCase(Locale.ROOT));
                sb.append(" + ");
                sb.append(number);
                break;
            case PROGRAM:
                sb.append(lvalue.toLowerCase(Locale.ROOT));
                sb.append(" := ");
                sb.append(program);
                sb.append('(');
                if (params != null) {
                    boolean first = true;
                    for (String param : params) {
                        if (first) { first = false; } else { sb.append(", "); }
                        sb.append(param.toLowerCase(Locale.ROOT));
                    }
                }
                sb.append(')');
                break;
        }
        return sb.toString();
    }

    private static String escaped(String val) {
        return val.replace("\r\n", "\n")
                .replace("\n", "\\n")
                .replace("\"", "\\\"");
    }
}
