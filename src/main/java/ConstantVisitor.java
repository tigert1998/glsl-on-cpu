import ast.*;
import ast.operators.*;
import ast.values.*;
import java.util.*;

public class ConstantVisitor extends LangBaseVisitor<Value> {
    private Scope scope = null;
    public Exception exception = null;

    public ConstantVisitor(Scope scope) {
        this.scope = scope;
    }

    private Value[] extractValues(int total, List<LangParser.ExprContext> exprCtxList) {
        Value[] values = new Value[total];
        var visitor = new ConstantVisitor(scope);
        for (int i = 0; i < total; i++) {
            values[i] = exprCtxList.get(i).accept(visitor);
            if (visitor.exception != null) {
                this.exception = visitor.exception;
                return null;
            }
        }
        return values;
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
        exception = new Exception("cannot apply operation on l-value here");
        return null;
    }

    @Override
    public Value visitPrefixUnaryExpr(LangParser.PrefixUnaryExprContext ctx) {
        if (ctx.INCREMENT() != null || ctx.DECREMENT() != null) {
            exception = new Exception("cannot apply operation on l-value here");
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

    @Override
    public Value visitMultDivModBinaryExpr(LangParser.MultDivModBinaryExprContext ctx) {
        var values = extractValues(2, ctx.expr());
        if (values == null) return null;
        BinaryOperator op = null;
        if (ctx.MULT() != null) {
            op = Mult.OP;
        } else if (ctx.DIV() != null) {
            op = Div.OP;
        } else {
            op = Mod.OP;
        }
        try {
            return op.apply(values[0], values[1], scope);
        } catch (Exception exception) {
            this.exception = exception;
            return null;
        }
    }

    @Override
    public Value visitPlusMinusBinaryExpr(LangParser.PlusMinusBinaryExprContext ctx) {
        var values = extractValues(2, ctx.expr());
        if (values == null) return null;
        BinaryOperator op = ctx.PLUS() != null ? Plus.OP : Minus.OP;
        try {
            return op.apply(values[0], values[1], scope);
        } catch (Exception exception) {
            this.exception = exception;
            return null;
        }
    }
}
