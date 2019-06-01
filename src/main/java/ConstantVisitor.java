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

    private Value extractValueWithBinaryOperator(Token opToken, List<LangParser.ExprContext> exprCtxList) {
        var values = extractValues(exprCtxList);
        if (values == null) return null;
        BinaryOperator op = (BinaryOperator) Operator.fromText(opToken.getText());
        try {
            return op.apply(values[0], values[1]);
        } catch (OperatorCannotBeAppliedException exception) {
            this.exception = new SyntaxErrorException(opToken, exception);
            return null;
        } catch (ArithmeticException exception) {
            this.exception = new SyntaxErrorException(opToken, exception.getMessage());
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
        var result = scope.lookupValue(ctx.IDENTIFIER().getText());
        if (result == null) {
            this.exception = SyntaxErrorException.undeclaredID(ctx.start, ctx.IDENTIFIER().getText());
            return null;
        } else if (result.value == null) {
            this.exception = SyntaxErrorException.constantExpressionRequired(ctx.start);
            return null;
        }
        return result.value;
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
            return type.construct(values);
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
            return type.construct(values);
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
            this.exception = SyntaxErrorException.invalidSubscriptingType(ctx.start, x.getType());
            return null;
        }
    }

    @Override
    public Value visitMemberFunctionInvocationExpr(LangParser.MemberFunctionInvocationExprContext ctx) {
        // only length
        var method = ctx.method;
        if (method.expr().size() >= 1 || !method.IDENTIFIER().getText().equals("length")) {
            this.exception = SyntaxErrorException.invalidMethod(
                    ctx.method.start, method.IDENTIFIER().getText());
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
            this.exception = SyntaxErrorException.invalidSelectionType(ctx.start, x.getType());
            return null;
        }
    }

    @Override
    public Value visitPrefixUnaryExpr(LangParser.PrefixUnaryExprContext ctx) {
        if (ctx.INCREMENT() != null || ctx.DECREMENT() != null) {
            exception = SyntaxErrorException.lvalueRequired(ctx.start);
            return null;
        }
        var value = extractValue(ctx.expr());
        if (exception != null) return null;
        UnaryOperator op = (UnaryOperator) Operator.fromText(ctx.op.getText());
        try {
            return op.apply(value);
        } catch (OperatorCannotBeAppliedException exception) {
            this.exception = new SyntaxErrorException(ctx.op, exception);
            return null;
        }
    }

    @Override
    public Value visitMultDivModBinaryExpr(LangParser.MultDivModBinaryExprContext ctx) {
        return extractValueWithBinaryOperator(ctx.op, ctx.expr());
    }

    @Override
    public Value visitPlusMinusBinaryExpr(LangParser.PlusMinusBinaryExprContext ctx) {
        return extractValueWithBinaryOperator(ctx.op, ctx.expr());
    }

    @Override
    public Value visitShlShrBinaryExpr(LangParser.ShlShrBinaryExprContext ctx) {
        return extractValueWithBinaryOperator(ctx.op, ctx.expr());
    }

    @Override
    public Value visitLessGreaterBinaryExpr(LangParser.LessGreaterBinaryExprContext ctx) {
        return extractValueWithBinaryOperator(ctx.op, ctx.expr());
    }

    @Override
    public Value visitEqNeqBinaryExpr(LangParser.EqNeqBinaryExprContext ctx) {
        return extractValueWithBinaryOperator(ctx.op, ctx.expr());
    }

    @Override
    public Value visitBitwiseAndBinaryExpr(LangParser.BitwiseAndBinaryExprContext ctx) {
        return extractValueWithBinaryOperator(ctx.op, ctx.expr());
    }

    @Override
    public Value visitBitwiseXorBinaryExpr(LangParser.BitwiseXorBinaryExprContext ctx) {
        return extractValueWithBinaryOperator(ctx.op, ctx.expr());
    }

    @Override
    public Value visitBitwiseOrBinaryExpr(LangParser.BitwiseOrBinaryExprContext ctx) {
        return extractValueWithBinaryOperator(ctx.op, ctx.expr());
    }

    @Override
    public Value visitLogicalAndBinaryExpr(LangParser.LogicalAndBinaryExprContext ctx) {
        return extractValueWithBinaryOperator(ctx.op, ctx.expr());
    }

    @Override
    public Value visitLogicalXorBinaryExpr(LangParser.LogicalXorBinaryExprContext ctx) {
        return extractValueWithBinaryOperator(ctx.op, ctx.expr());
    }

    @Override
    public Value visitLogicalOrBinaryExpr(LangParser.LogicalOrBinaryExprContext ctx) {
        return extractValueWithBinaryOperator(ctx.op, ctx.expr());
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
