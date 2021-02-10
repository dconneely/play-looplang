package uk.conneely.toylang.interpreter;

import java.util.List;
import uk.conneely.toylang.ast.Node;
import uk.conneely.toylang.token.Token;

/**
 * Our interpreter context for a call. Note that procedure definitions and variable definitions are separate namespaces.
 * Procedure definitions are global, but variable definitions are new in each called context (apart from the parameters,
 * which are passed by reference).
 */
public interface Context {
    void procedure(String name, List<String> paramList, List<Node> body);

    List<String> procedureParamDefs(String name);

    List<Node> procedureBody(String name);

    void variable(String name, int newValue);

    int variable(String name);

    Context procedureCall(String name, List<Token> paramValues, int defaultValue);
}
