import ast.*;
import ast.operators.*;
import ast.values.*;

import java.util.*;

public class ConstantListener extends LangBaseListener {
    private Stack<Value> stack = new Stack<>();
    private Scope scope = null;
    private List<Exception> exceptions = new ArrayList<>();

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
        exceptions.add(new Exception("can apply operation on l-value here"));
    }

    @Override
    public void exitPrefixUnaryExpr(LangParser.PrefixUnaryExprContext ctx) {
        if (ctx.INCREMENT() != null || ctx.DECREMENT() != null) {
            exceptions.add(new Exception("can apply operation on l-value here"));
            return;
        }
        var value = stack.pop();
        UnaryOperator op = null;
        if (ctx.PLUS() != null) {
            op = Plus.OP;
        } else if (ctx.MINUS() != null) {
            op = Minus.OP;
        } else if (ctx.LOGICAL_NOT() != null) {
            op = LogicalNot.OP;
        } else {
            op = BitwiseNot.OP;
        }
        try {
            stack.push(op.apply(value, scope));
        } catch (Exception exception) {
            exceptions.add(exception);
        }
    }
}
