package com.davidconneely.looplang.parser;

import com.davidconneely.looplang.ast.Node;

import java.io.IOException;

public interface Parser {
    Node next() throws IOException;
}
