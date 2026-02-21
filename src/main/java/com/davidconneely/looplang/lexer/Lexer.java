package com.davidconneely.looplang.lexer;

import com.davidconneely.looplang.token.Token;
import java.io.IOException;

public interface Lexer {
  void pushback(Token token);

  /**
   * @return null for EOF.
   */
  Token next() throws IOException;
}
