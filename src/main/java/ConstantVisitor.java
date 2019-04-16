import ast.*;
import ast.exceptions.*;
import ast.operators.*;
import ast.values.*;
import ast.types.*;
import org.antlr.v4.runtime.Token;

import java.util.*;

public class ConstantVisitor extends LangBaseVisitor<Value> {
    private Scope scope = null;
    public SyntaxErrorException exception = null;

    public ConstantVisitor(Scope scope) {
        this.scope = scope;
    }

    // utilities
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

    private Value applyBinaryOperator(Token opToken, BinaryOperator op, Value[] values) {
        try {
            return op.apply(values[0], values[1]);
        } catch (OperatorCannotBeAppliedException exception) {
            this.exception = new SyntaxErrorException(opToken, exception);
            return null;
        }
    }

    private Value applyUnaryOperator(Token opToken, UnaryOperator op, Value value) {
        try {
            return op.apply(value);
        } catch (OperatorCannotBeAppliedException exception) {
            this.exception = new SyntaxErrorException(opToken, exception);
            return null;
        }
    }

    // visiting methods
    @Override
    public Value visitLiteralExpr(LangParser.LiteralExprContext ctx) {
        return Utility.valueFromLiteralExprContext(ctx);
    }

    @Override
    public Value visitReferenceExpr(LangParser.ReferenceExprContext ctx) {
        if (!scope.constants.containsKey(ctx.IDENTIFIER().getText())) {
            this.exception = SyntaxErrorException.undeclaredID(ctx.start, ctx.IDENTIFIER().getText());
            return null;
        }
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
    public Value visitMemberFunctionInvocationExpr(LangParser.MemberFunctionInvocationExprContext ctx) {
        // only length
        var method = ctx.method;
        if (method.expr().size() >= 1 ||
                method.structType().IDENTIFIER() == null ||
                method.structType().specifiedArrayLength() != null ||
                !method.structType().IDENTIFIER().getText().equals("length")) {
            this.exception = SyntaxErrorException.invalidMethod(
                    ctx.method.start, method.structType().IDENTIFIER().getText());
            return null;
        }
        var value = extractValue(ctx.expr());
        if (value == null) return null;
        if (!(value instanceof ArrayValue)) {
            this.exception = SyntaxErrorException.lengthOnlyArrays(ctx.start);
            return null;
        } else {
            return new IntValue(((ArrayValue) value).values.length);
        }
    }

    @Override
    public Value visitElementSelectionExpr(LangParser.ElementSelectionExprContext ctx) {
        Value x = extractValue(ctx.expr());
        if (x == null) return null;
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
        return applyUnaryOperator(ctx.op, op, value);
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
        return applyBinaryOperator(ctx.op, op, values);
    }

    @Override
    public Value visitPlusMinusBinaryExpr(LangParser.PlusMinusBinaryExprContext ctx) {
        var values = extractValues(ctx.expr());
        if (values == null) return null;
        BinaryOperator op = ctx.PLUS() != null ? Plus.OP : Minus.OP;
        return applyBinaryOperator(ctx.op, op, values);
    }

    @Override
    public Value visitShlShrBinaryExpr(LangParser.ShlShrBinaryExprContext ctx) {
        var values = extractValues(ctx.expr());
        if (values == null) return null;
        BinaryOperator op = ctx.SHL() != null ? Shl.OP : Shr.OP;
        return applyBinaryOperator(ctx.op, op, values);
    }

    @Override
    public Value visitLessGreaterBinaryExpr(LangParser.LessGreaterBinaryExprContext ctx) {
        var values = extractValues(ctx.expr());
        if (values == null) return null;
        BinaryOperator op;
        if (ctx.LESS() != null)
            op = Less.OP;
        else if (ctx.LESS_EQUAL() != null)
            op = LessEqual.OP;
        else if (ctx.GREATER() != null)
            op = Greater.OP;
        else op = GreaterEqual.OP;
        return applyBinaryOperator(ctx.op, op, values);
    }

    @Override
    public Value visitEqNeqBinaryExpr(LangParser.EqNeqBinaryExprContext ctx) {
        var values = extractValues(ctx.expr());
        if (values == null) return null;
        BinaryOperator op;
        if (ctx.EQUAL() != null)
            op = Equal.OP;
        else op = NotEqual.OP;
        return applyBinaryOperator(ctx.op, op, values);
    }

    @Override
    public Value visitLogicalAndBinaryExpr(LangParser.LogicalAndBinaryExprContext ctx) {
        var values = extractValues(ctx.expr());
        if (values == null) return null;
        BinaryOperator op = LogicalAnd.OP;
        return applyBinaryOperator(ctx.op, op, values);
    }

    @Override
    public Value visitLogicalXorBinaryExpr(LangParser.LogicalXorBinaryExprContext ctx) {
        var values = extractValues(ctx.expr());
        if (values == null) return null;
        BinaryOperator op = LogicalXor.OP;
        return applyBinaryOperator(ctx.op, op, values);
    }

    @Override
    public Value visitLogicalOrBinaryExpr(LangParser.LogicalOrBinaryExprContext ctx) {
        var values = extractValues(ctx.expr());
        if (values == null) return null;
        BinaryOperator op = LogicalOr.OP;
        return applyBinaryOperator(ctx.op, op, values);
    }

    @Override
    public Value visitTernaryConditionalExpr(LangParser.TernaryConditionalExprContext ctx) {
        var values = extractValues(ctx.expr());
        if (values == null) return null;
        if (!(values[0] instanceof BoolValue)) {
            this.exception = SyntaxErrorException.notBooleanExpression(ctx.expr(0).start);
            return null;
        }
        if (!values[1].getType().equals(values[2].getType())) {
            this.exception = SyntaxErrorException.cannotConvert(ctx.expr(1).start,
                    values[2].getType(), values[1].getType());
            return null;
        }
        return ((BoolValue) values[0]).value ? values[1] : values[2];
    }

    @Override
    public Value visitAssignExpr(LangParser.AssignExprContext ctx) {
        this.exception = SyntaxErrorException.lvalueRequired(ctx.start);
        return null;
    }

    @Override
    public Value visitParameteredExpr(LangParser.ParameteredExprContext ctx) {
        var value = extractValue(ctx.expr());
        if (exception != null) return null;
        return value;
    }
}
