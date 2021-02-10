package uk.conneely.toylang.interpreter;

import uk.conneely.toylang.ToyException;
import uk.conneely.toylang.ast.Node;

final class DefaultInterpreter implements Interpreter {
    private final Context context;

    DefaultInterpreter(final Context context) {
        this.context = context;
    }

    @Override
    public void interpret(final Node node) {
        try {
            node.interpret(this.context);
        } catch (ToyException e) {
            e.printStackTrace();
        }
    }
}
