package uk.conneely.toylang;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.petitparser.parser.Parser;
import org.petitparser.parser.combinators.SettableParser;
import org.petitparser.tools.ExpressionBuilder;

import static org.junit.jupiter.api.Assertions.*;
import static org.petitparser.parser.primitive.CharacterParser.*;

/**
 * Run the examples in the java-petitparser README on GitHub.
 * Acts as an API change "canary".
 */
public final class PetitParserTest {
    @Test
    public void testIdParser() {
        Parser id = letter().seq(letter().or(digit()).star());
        assertTrue(id.parse("yeah").isSuccess());
        assertTrue(id.parse("f12").isSuccess());
        assertTrue(id.parse("123").isFailure());
    }

    @Test
    public void testWordParser() {
        Parser id = letter().seq(word().star()).flatten();
        List<Object> matches = id.matchesSkipping("foo 123 bar4");
        assertEquals(Arrays.asList("foo", "bar4"), matches);
    }

    @Test
    public void testExprParser() {
        Parser number = digit().plus().flatten().trim().map((String value) -> Integer.parseInt(value));

        SettableParser term = SettableParser.undefined();
        SettableParser prod = SettableParser.undefined();
        SettableParser prim = SettableParser.undefined();

        term.set(prod.seq(of('+').trim()).seq(term).map((List<Integer> values) -> {
            return values.get(0) + values.get(2);
        }).or(prod));
        prod.set(prim.seq(of('*').trim()).seq(prod).map((List<Integer> values) -> {
            return values.get(0) * values.get(2);
        }).or(prim));
        prim.set((of('(').trim().seq(term).seq(of(')').trim())).map((List<Integer> values) -> {
            return values.get(1);
        }).or(number));

        Parser start = term.end();

        assertEquals(7, (Integer) start.parse("1 + 2 * 3").get());
        assertEquals(9, (Integer) start.parse("(1 + 2) * 3").get());
    }

    @Test
    public void testExprBuilder() {
        ExpressionBuilder builder = new ExpressionBuilder();
        // support for parentheses
        builder.group()
                .primitive(digit().plus().seq(of('.')
                        .seq(digit().plus()).optional())
                        .flatten().trim().map(Double::parseDouble))
                .wrapper(of('(').trim(), of(')').trim(),
                        (List<Double> values) -> values.get(1));
        // negation is a prefix operator
        builder.group()
                .prefix(of('-').trim(), (List<Double> values) -> -values.get(1));
        // power is right-associative
        builder.group()
                .right(of('^').trim(), (List<Double> values) -> Math.pow(values.get(0), values.get(2)));
        // multiplication and addition are left-associative
        builder.group()
                .left(of('*').trim(), (List<Double> values) -> values.get(0) * values.get(2))
                .left(of('/').trim(), (List<Double> values) -> values.get(0) / values.get(2));
        builder.group()
                .left(of('+').trim(), (List<Double> values) -> values.get(0) + values.get(2))
                .left(of('-').trim(), (List<Double> values) -> values.get(0) - values.get(2));

        Parser parser = builder.build().end();

        assertEquals(-8.0, parser.parse("-8").get());
        assertEquals(7.0, parser.parse("1+2*3").get());
        assertEquals(5.0, parser.parse("1*2+3").get());
        assertEquals(1.0, parser.parse("8/4/2").get());
        assertEquals(256.0, parser.parse("2^2^3").get());
    }
}
