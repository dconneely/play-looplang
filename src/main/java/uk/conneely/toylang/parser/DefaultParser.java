package uk.conneely.toylang.parser;

import java.io.IOException;
import uk.conneely.toylang.ast.Node;
import uk.conneely.toylang.ast.NodeFactory;
import uk.conneely.toylang.lexer.Lexer;
import uk.conneely.toylang.token.Token;

final class DefaultParser implements Parser {
    private final Lexer tokens;
    private final Token.Kind until;

    DefaultParser(final Lexer tokens, final Token.Kind until) {
        this.tokens = tokens;
        this.until = until;
    }

    @Override
    public Node next() throws IOException {
        Node node;
        while (true) {
            Token token = tokens.next();
            switch (token.kind()) {
                case KWDEF:
                    node = NodeFactory.def();
                    break;
                case KWFOR:
                    node = NodeFactory.for_();
                    break;
                case KWINC:
                    node = NodeFactory.inc();
                    break;
                case KWPRINTLN:
                    node = NodeFactory.println();
                    break;
                case KWSTZ:
                    node = NodeFactory.stz();
                    break;
                case IDENT:
                    node = NodeFactory.call();
                    break;
                case CMMNT:
                case LNSEP:
                case STSEP:
                    continue;
                default:
                    if (token.kind() == until) {
                        node = null;
                        break;
                    }
                    throw new ParserException("unexpected token at start of statement, " + token);
            }
            if (node != null) {
                tokens.pushback(token);
                node.parse(tokens);
            }
            return node;
        }
    }
}
