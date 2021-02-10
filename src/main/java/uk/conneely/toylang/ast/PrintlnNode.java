package uk.conneely.toylang.ast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import uk.conneely.toylang.interpreter.Context;
import uk.conneely.toylang.interpreter.InterpreterException;
import uk.conneely.toylang.lexer.Lexer;
import uk.conneely.toylang.parser.ParserException;
import uk.conneely.toylang.token.Token;

final class PrintlnNode implements Node {
    private List<Token> paramVals;

    @Override
    public void parse(final Lexer tokens) throws IOException {
        Token token = tokens.next();
        if (token.kind() != Token.Kind.KWPRINTLN) {
            throw new IllegalStateException("println: expected `println`; got " + token);
        }
        this.paramVals = new ArrayList<>();
        while (true) {
            token = tokens.next();
            Token.Kind kind = token.kind();
            if (kind == Token.Kind.IDENT || kind == Token.Kind.INTNUM || kind == Token.Kind.STRLIT) {
                this.paramVals.add(token);
            } else if (token.endsStmt()) {
                tokens.pushback(token);
                break;
            } else {
                throw new ParserException("println: expected identifier, number, string or end statement; got " + token);
            }
        }
    }

    @Override
    public void interpret(final Context context) {
        final StringBuilder sb = new StringBuilder();
        Token.Kind prev = Token.Kind.STRLIT;
        for (Token paramVal : this.paramVals) {
            Token.Kind kind = paramVal.kind();
            if (prev != Token.Kind.STRLIT && kind != Token.Kind.STRLIT) {
                sb.append(' ');
            }
            prev = kind;
            switch (kind) {
                case IDENT:
                    try {
                        String name = paramVal.textValue();
                        int val = context.variable(name);
                        sb.append(val);
                    } catch (InterpreterException e) {
                        sb.append("undefined");
                    }
                    break;
                case INTNUM:
                    sb.append(paramVal.intValue());
                    break;
                case STRLIT:
                    sb.append(paramVal.textValue());
                    break;
                default:
                    throw new InterpreterException("println: expected identifier, number or string; got " + paramVal);
            }
        }
        System.out.println(sb.toString());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("print");
        for (Token paramVal : this.paramVals) {
            sb.append(' ');
            if (paramVal.kind() == Token.Kind.STRLIT) {
                sb.append('\"');
                sb.append(paramVal.textValue());
                sb.append('\"');
            } else {
                sb.append(paramVal.textValue());
            }
        }
        return sb.toString();
    }
}
