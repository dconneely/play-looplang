package uk.conneely.toylang.lexer;

import java.io.IOException;
import java.io.Reader;
import java.util.Deque;
import java.util.LinkedList;

final class ReaderCharInput implements CharInput {
    private final Reader reader;
    private final Deque<Integer> lookahead;

    ReaderCharInput(final Reader reader) {
        this.reader = reader;
        this.lookahead = new LinkedList<>();
    }

    @Override
    public void pushback(final int ch) {
        this.lookahead.push(ch);
    }

    @Override
    public int next() throws IOException {
        if (!this.lookahead.isEmpty()) {
            return this.lookahead.pop();
        }
        return reader.read();
    }
}
