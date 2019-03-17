import ast.values.*;

import java.util.*;

public class ConstantListener extends LangBaseListener {
    private Stack<Value> stack;

    @Override
    public void exitLiteralExpr(LangParser.LiteralExprContext ctx) {
        stack.push(Utility.valueFromLiteralExprContext(ctx));
    }
}
