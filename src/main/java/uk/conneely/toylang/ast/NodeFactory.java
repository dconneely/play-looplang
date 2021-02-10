package uk.conneely.toylang.ast;

public final class NodeFactory {
    private NodeFactory() {
    }

    public static Node call() {
        return new CallNode();
    }

    public static Node def() {
        return new DefNode();
    }

    public static Node for_() {
        return new ForNode();
    }

    public static Node inc() {
        return new IncNode();
    }

    public static Node println() {
        return new PrintlnNode();
    }

    public static Node stz() {
        return new StzNode();
    }
}
