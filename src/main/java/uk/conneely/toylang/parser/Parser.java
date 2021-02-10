package uk.conneely.toylang.parser;

import java.io.IOException;
import uk.conneely.toylang.ast.Node;

public interface Parser {
    Node next() throws IOException;
}
