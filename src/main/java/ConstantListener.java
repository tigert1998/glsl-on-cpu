import ast.*;
import ast.values.*;
import ast.types.*;

import java.util.*;

public class ConstantListener extends LangBaseListener {
    private Stack<Value> stack = new Stack<>();
    private Scope scope = null;

    public ConstantListener(Scope scope) {
        this.scope = scope;
    }

    @Override
    public void exitLiteralExpr(LangParser.LiteralExprContext ctx) {
        stack.push(Utility.valueFromLiteralExprContext(ctx));
    }

    @Override
    public void exitReferenceExpr(LangParser.ReferenceExprContext ctx) {
        stack.push(scope.constants.get(ctx.IDENTIFIER().getText()));
    }

    @Override
    public void exitPostfixUnaryExpr(LangParser.PostfixUnaryExprContext ctx) {
        Main.error("++ or -- not supported here");
    }

    @Override
    public void exitPrefixUnaryExpr(LangParser.PrefixUnaryExprContext ctx) {
        if (ctx.INCREMENT() != null || ctx.DECREMENT() != null) {
            Main.error("++ or -- not supported here");
        }
        
    }
}
