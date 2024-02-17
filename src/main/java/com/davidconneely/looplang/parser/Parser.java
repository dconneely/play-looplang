package com.davidconneely.looplang.parser;

import java.io.IOException;

import com.davidconneely.looplang.ast.Node;

public interface Parser {
    Node next() throws IOException;
}
