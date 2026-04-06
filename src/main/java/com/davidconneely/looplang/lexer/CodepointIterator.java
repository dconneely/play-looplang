package com.davidconneely.looplang.lexer;

/** A Unicode codepoint iterator that supports single-element pushback and resource management. */
public interface CodepointIterator extends AutoCloseable {
  /**
   * Push back a codepoint so it will be returned by the next call to {@link #nextCodepoint()}.
   *
   * @param cp the Unicode codepoint to push back.
   */
  void pushback(int cp);

  /**
   * Check if there are more Unicode codepoints available.
   *
   * @return true if there is a codepoint available, false at EOF.
   */
  boolean hasNext();

  /**
   * Get the next Unicode codepoint.
   *
   * @return the next Unicode codepoint.
   * @throws java.util.NoSuchElementException if there are no more codepoints.
   */
  int nextCodepoint();

  @Override
  void close();
}
