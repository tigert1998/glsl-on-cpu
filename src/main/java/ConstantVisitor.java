import ast.*;
import ast.exceptions.*;
import ast.operators.*;
import ast.types.Type;
import ast.values.*;
import org.antlr.v4.runtime.Token;

import java.util.*;

public class ConstantVisitor extends LangBaseVisitor<Value> {
    private Scope scope = null;
    public SyntaxErrorException exception = null;

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

    private Value extractValue(LangParser.ExprContext exprCtx) {
        var visitor = new ConstantVisitor(scope);
        var value = exprCtx.accept(visitor);
        if (visitor.exception != null)
            this.exception = visitor.exception;
        return value;
    }

    private boolean checkBinaryOperatorApplicable(Token token, BinaryOperator op, Value[] values) {
        Type type1 = values[0].getType(), type2 = values[1].getType();
        if (op.canBeApplied(type1, type2)) return true;
        this.exception = new OperatorCannotBeAppliedException(token, (Operator) op, type1, type2);
        return false;
    }

    private boolean checkUnaryOperatorApplicable(Token token, UnaryOperator op, Value value) {
        var type = value.getType();
        if (op.canBeApplied(type)) return true;
        this.exception = new OperatorCannotBeAppliedException(token, (Operator) op, type);
        return false;
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
        exception = SyntaxErrorException.lvalueRequired(ctx.stop);
        return null;
    }

    @Override
    public Value visitPrefixUnaryExpr(LangParser.PrefixUnaryExprContext ctx) {
        if (ctx.INCREMENT() != null || ctx.DECREMENT() != null) {
            exception = SyntaxErrorException.lvalueRequired(ctx.start);
            return null;
        }
        var value = extractValue(ctx.expr());
        if (exception != null) return null;
        UnaryOperator op;
        if (ctx.PLUS() != null) {
            op = Plus.OP;
        } else if (ctx.MINUS() != null) {
            op = Minus.OP;
        } else if (ctx.LOGICAL_NOT() != null) {
            op = LogicalNot.OP;
        } else {
            op = BitwiseNot.OP;
        }
        if (!checkUnaryOperatorApplicable(ctx.op, op, value)) return null;
        return op.apply(value, scope);
    }

    @Override
    public Value visitMultDivModBinaryExpr(LangParser.MultDivModBinaryExprContext ctx) {
        var values = extractValues(2, ctx.expr());
        if (values == null) return null;
        BinaryOperator op;
        if (ctx.MULT() != null) {
            op = Mult.OP;
        } else if (ctx.DIV() != null) {
            op = Div.OP;
        } else {
            op = Mod.OP;
        }
        if (!checkBinaryOperatorApplicable(ctx.op, op, values)) return null;
        return op.apply(values[0], values[1], scope);
    }

    @Override
    public Value visitPlusMinusBinaryExpr(LangParser.PlusMinusBinaryExprContext ctx) {
        var values = extractValues(2, ctx.expr());
        if (values == null) return null;
        BinaryOperator op = ctx.PLUS() != null ? Plus.OP : Minus.OP;
        if (!checkBinaryOperatorApplicable(ctx.op, op, values)) return null;
        return op.apply(values[0], values[1], scope);
    }

    @Override
    public Value visitParameteredExpr(LangParser.ParameteredExprContext ctx) {
        var value = extractValue(ctx.expr());
        if (exception != null) return null;
        return value;
    }
}
