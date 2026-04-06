package com.davidconneely.looplang.lexer;

/** An input stream of Unicode codepoints. */
public interface CodepointInput {
  /**
   * Push back a codepoint to the input stream.
   *
   * @param cp the Unicode codepoint to push back.
   */
  void pushback(int cp);

  /**
   * Get the next Unicode codepoint from the input stream.
   *
   * @return the next Unicode codepoint, or -1 for EOF.
   */
  int next() throws java.io.IOException;
}
