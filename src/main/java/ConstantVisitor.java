import ast.*;
import ast.exceptions.*;
import ast.operators.*;
import ast.values.*;
import ast.types.*;

import java.util.*;

public class ConstantVisitor extends LangBaseVisitor<Value> {
    private Scope scope = null;
    public SyntaxErrorException exception = null;

    public ConstantVisitor(Scope scope) {
        this.scope = scope;
    }

    private Value[] extractValues(List<LangParser.ExprContext> exprCtxList) {
        int total = exprCtxList.size();
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

    @Override
    public Value visitLiteralExpr(LangParser.LiteralExprContext ctx) {
        return Utility.valueFromLiteralExprContext(ctx);
    }

    @Override
    public Value visitReferenceExpr(LangParser.ReferenceExprContext ctx) {
        return scope.constants.get(ctx.IDENTIFIER().getText());
    }

    @Override
    public Value visitBasicTypeConstructorInvocationExpr(LangParser.BasicTypeConstructorInvocationExprContext ctx) {
        var invocationCtx = ctx.basicTypeConstructorInvocation();
        var values = extractValues(invocationCtx.expr());
        if (values == null) return null;
        Type type;
        try {
            type = Utility.typeFromBasicTypeContext(invocationCtx.basicType(), scope);
        } catch (SyntaxErrorException exception) {
            this.exception = exception;
            return null;
        }
        try {
            return Value.constructor(type, values);
        } catch (ConstructionFailedException exception) {
            this.exception = new SyntaxErrorException(ctx.start, exception);
            return null;
        }
    }

    @Override
    public Value visitFunctionOrStructConstructorInvocationExpr(
            LangParser.FunctionOrStructConstructorInvocationExprContext ctx) {
        var invocationCtx = ctx.functionOrStructConstructorInvocation();
        var values = extractValues(invocationCtx.expr());
        if (values == null) return null;
        Type type;
        try {
            type = Utility.typeFromStructTypeContext(invocationCtx.structType(), scope);
        } catch (SyntaxErrorException exception) {
            // built in function not supported yet
            this.exception = SyntaxErrorException.lvalueRequired(ctx.start);
            return null;
        }
        try {
            return Value.constructor(type, values);
        } catch (ConstructionFailedException exception) {
            this.exception = new SyntaxErrorException(ctx.start, exception);
            return null;
        }
    }

    @Override
    public Value visitArraySubscriptingExpr(LangParser.ArraySubscriptingExprContext ctx) {
        Value x = extractValue(ctx.expr(0));
        if (x == null) return null;
        int idx;
        try {
            idx = Utility.evalExprAsIntegral(ctx.idx, scope);
        } catch (SyntaxErrorException exception) {
            this.exception = exception;
            return null;
        }
        if (x instanceof Indexed) {
            try {
                return ((Indexed) x).valueAt(idx);
            } catch (InvalidIndexException exception) {
                this.exception = new SyntaxErrorException(ctx.idx.start, exception);
                return null;
            }
        } else {
            this.exception = SyntaxErrorException.invalidSubscriptingType(ctx.start, ctx.expr(0).getText());
            return null;
        }
    }

    @Override
    public Value visitElementSelectionExpr(LangParser.ElementSelectionExprContext ctx) {
        Value x = extractValue(ctx.expr());
        if (x instanceof Selected) {
            try {
                return ((Selected) x).select(ctx.IDENTIFIER().getText());
            } catch (InvalidSelectionException exception) {
                this.exception = new SyntaxErrorException(ctx.selection, exception);
                return null;
            }
        } else {
            this.exception = SyntaxErrorException.invalidSelectionType(ctx.start, ctx.expr().getText());
            return null;
        }
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
        try {
            return op.apply(value);
        } catch (OperatorCannotBeAppliedException exception) {
            this.exception = new SyntaxErrorException(ctx.op, exception);
            return null;
        }
    }

    @Override
    public Value visitMultDivModBinaryExpr(LangParser.MultDivModBinaryExprContext ctx) {
        var values = extractValues(ctx.expr());
        if (values == null) return null;
        BinaryOperator op;
        if (ctx.MULT() != null) {
            op = Mult.OP;
        } else if (ctx.DIV() != null) {
            op = Div.OP;
        } else {
            op = Mod.OP;
        }
        try {
            return op.apply(values[0], values[1]);
        } catch (OperatorCannotBeAppliedException exception) {
            this.exception = new SyntaxErrorException(ctx.op, exception);
            return null;
        }
    }

    @Override
    public Value visitPlusMinusBinaryExpr(LangParser.PlusMinusBinaryExprContext ctx) {
        var values = extractValues(ctx.expr());
        if (values == null) return null;
        BinaryOperator op = ctx.PLUS() != null ? Plus.OP : Minus.OP;
        try {
            return op.apply(values[0], values[1]);
        } catch (OperatorCannotBeAppliedException exception) {
            this.exception = new SyntaxErrorException(ctx.op, exception);
            return null;
        }
    }

    @Override
    public Value visitParameteredExpr(LangParser.ParameteredExprContext ctx) {
        var value = extractValue(ctx.expr());
        if (exception != null) return null;
        return value;
    }
}