package uk.conneely.toylang;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import uk.conneely.toylang.ast.Node;
import uk.conneely.toylang.interpreter.Context;
import uk.conneely.toylang.interpreter.Interpreter;
import uk.conneely.toylang.interpreter.InterpreterFactory;
import uk.conneely.toylang.lexer.Lexer;
import uk.conneely.toylang.lexer.LexerFactory;
import uk.conneely.toylang.parser.Parser;
import uk.conneely.toylang.parser.ParserFactory;
import uk.conneely.toylang.token.Token;

public final class Main {
    public static void main(final String[] args) throws IOException {
        final Reader reader = new StringReader(""
                + "def add x y (for y (inc x))  # x := x + y\n"
                + "def let x y (stz x; add x y)  # x := y\n"
                + "def swap x y (let z x; let x y; let y z)  # x,y := y,x\n"
                + "def let_add x y z (stz x; add x y; add x z)  # x := y + z\n"
                + "def let_true x y (stz x; for y (stz x; inc x))  # x := (y != 0) ? 1 : 0\n"
                + "def let_not x y (stz x; inc x; for y (stz x))  # x := (y == 0) ? 1 : 0\n"
                + "def dec x (stz y; for x (let z y; inc y); let x z)  # x := x - 1\n"
                + "def sub x y (for y (dec x))  # x := x - y\n"
                + "def let_sub x y z (let x y; for z (dec x))  # x := y - z\n"
                + "def let_gt x y z (let_sub t y z; let_true x t)  # x := (y > z) ? 1 : 0\n"
                + "def mul x y (dec y; for x (add x y); inc y)  # x := x * y (if y != 0)\n"
                + "def let_mul x y z (stz x; for y (add x z))  # x := y * z\n"
                + "def div_rec x y r (let_true t x; for t ( inc r; sub x y; div_rec x y r ) )  # helper\n"
                + "def div x y (inc x; sub x y; stz r; div_rec x y r; let x r)  # x := x / y\n"
                + "def let_div x y z (let yp y; inc yp; sub yp z; stz x; div_rec yp z x)  # x := y / z\n"
                + "\n"
                + "let_div x 100 26\n"
                + "println 100 \" / \" 26 \" = \" x\n"
        );
        final Lexer tokens = LexerFactory.lexer(reader);
        final Parser parser = ParserFactory.parser(tokens, Token.Kind.EOF);
        final Context context = InterpreterFactory.context();
        final Interpreter interpreter = InterpreterFactory.interpreter(context);
        while (true) {
            Node node = parser.next();
            if (node == null) {
                break;
            }
            interpreter.interpret(node);
        }
    }
}
