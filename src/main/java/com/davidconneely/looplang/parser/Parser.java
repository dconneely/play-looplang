package com.davidconneely.looplang.parser;

import com.davidconneely.looplang.statement.Statement;
import java.io.IOException;

public interface Parser {
  Statement next() throws IOException;
}
