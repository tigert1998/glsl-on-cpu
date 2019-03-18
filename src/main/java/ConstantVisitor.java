import ast.*;
import ast.operators.*;
import ast.values.*;

public class ConstantVisitor extends LangBaseVisitor<Value> {
    private Scope scope = null;
    private Exception exception = null;

    public ConstantVisitor(Scope scope) {
        this.scope = scope;
    }

    @Override
    public Value visitLiteralExpr(LangParser.LiteralExprContext ctx) {
        return Utility.valueFromLiteralExprContext(ctx);
    }

    @Override
    public Value visitReferenceExpr(LangParser.ReferenceExprContext ctx) {
        return scope.constants.get(ctx.IDENTIFIER().getText());
    }

    @Override
    public Value visitPostfixUnaryExpr(LangParser.PostfixUnaryExprContext ctx) {
        exception = new Exception("can apply operation on l-value here");
        return null;
    }

    @Override
    public Value visitPrefixUnaryExpr(LangParser.PrefixUnaryExprContext ctx) {
        if (ctx.INCREMENT() != null || ctx.DECREMENT() != null) {
            exception = new Exception("can apply operation on l-value here");
            return null;
        }
        var newVisitor = new ConstantVisitor(scope);
        var value = ctx.expr().accept(newVisitor);
        if (value == null) {
            exception = newVisitor.exception;
            return null;
        }
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
            return op.apply(value, scope);
        } catch (Exception exception) {
            this.exception = exception;
            return null;
        }
    }
}
