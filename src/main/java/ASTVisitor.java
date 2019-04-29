import ast.AST;
import ast.Scope;
import ast.exceptions.*;
import ast.expr.*;
import ast.operators.*;
import ast.stmt.*;
import ast.types.*;
import ast.values.*;
import org.antlr.v4.runtime.Token;

import java.util.List;

public class ASTVisitor extends LangBaseVisitor<AST> {
    private Scope scope;
    public SyntaxErrorException exception;

    public ASTVisitor(Scope scope) {
        this.scope = scope;
    }

    private Expr[] extractExprs(List<LangParser.ExprContext> exprCtxList) {
        int total = exprCtxList.size();
        Expr[] exprs = new Expr[total];
        var visitor = new ASTVisitor(scope);
        for (int i = 0; i < total; i++) {
            exprs[i] = (Expr) exprCtxList.get(i).accept(visitor);
            if (visitor.exception != null) {
                this.exception = visitor.exception;
                return null;
            }
        }
        return exprs;
    }

    private Expr extractExpr(LangParser.ExprContext exprCtx) {
        var visitor = new ASTVisitor(scope);
        var expr = (Expr) exprCtx.accept(visitor);
        if (visitor.exception != null)
            this.exception = visitor.exception;
        return expr;
    }

    private Expr extractBinaryExpr(Token opToken, List<LangParser.ExprContext> exprCtxList) {
        var exprs = extractExprs(exprCtxList);
        if (exprs == null) return null;
        BinaryOperator op = (BinaryOperator) Operator.fromText(opToken.getText());
        try {
            // check applicable
            op.apply(exprs[0].getType(), exprs[1].getType());
        } catch (OperatorCannotBeAppliedException exception) {
            this.exception = new SyntaxErrorException(opToken, exception);
            return null;
        }
        try {
            return BinaryExpr.factory(op, exprs);
        } catch (ArithmeticException exception) {
            this.exception = new SyntaxErrorException(opToken, exception.getMessage());
            return null;
        }
    }

    @Override
    public AST visitLiteralExpr(LangParser.LiteralExprContext ctx) {
        var value = Utility.valueFromLiteralExprContext(ctx);
        return new ConstExpr(value);
    }

    @Override
    public AST visitReferenceExpr(LangParser.ReferenceExprContext ctx) {
        String id = ctx.IDENTIFIER().getText();
        var result = scope.lookupConstantOrVariable(id);
        if (result == null) {
            this.exception = SyntaxErrorException.undeclaredID(ctx.start, id);
            return null;
        }
        if (result.value != null) {
            return new ConstExpr(result.value);
        } else {
            return new ReferenceExpr(result.stmt);
        }
    }

    @Override
    public AST visitBasicTypeConstructorInvocationExpr(LangParser.BasicTypeConstructorInvocationExprContext ctx) {
        var basicTypeConstructorInvocation = ctx.basicTypeConstructorInvocation();
        Type type;
        try {
            type = Utility.typeFromBasicTypeContext(basicTypeConstructorInvocation.basicType(), scope);
        } catch (SyntaxErrorException exception) {
            this.exception = exception;
            return null;
        }
        var exprs = extractExprs(basicTypeConstructorInvocation.expr());
        if (exprs == null) return null;
        var values = new Value[exprs.length];
        for (int i = 0; i < exprs.length; i++)
            values[i] = exprs[i].getType().getDefaultValue();
        try {
            // check syntax
            Value.constructor(type, values);
        } catch (ConstructionFailedException exception) {
            this.exception = new SyntaxErrorException(ctx.start, exception);
            return null;
        }

        return ConstructionExpr.factory(type, exprs);
    }

    @Override
    public AST visitArraySubscriptingExpr(LangParser.ArraySubscriptingExprContext ctx) {
        var array = extractExpr(ctx.expr(0));
        if (array == null) return null;
        if (!(array.getType().getDefaultValue() instanceof Indexed)) {
            this.exception = SyntaxErrorException.invalidSubscriptingType(ctx.start, ctx.expr(0).getText());
            return null;
        }

        var idx = extractExpr(ctx.expr(1));
        if (idx == null) return null;
        if (!(idx.getType() instanceof IntType || idx.getType() instanceof UintType)) {
            this.exception = SyntaxErrorException.notIntegerExpression(ctx.idx.start);
            return null;
        }

        if (idx instanceof ConstExpr) {
            try {
                int i = Utility.evalValueAsIntegral(((ConstExpr) idx).getValue(), ctx.idx.start);
                try {
                    ((Indexed) array.getType().getDefaultValue()).valueAt(i);
                } catch (InvalidIndexException exception) {
                    this.exception = new SyntaxErrorException(ctx.idx.start, exception);
                    return null;
                }
            } catch (SyntaxErrorException ignore) {
            }
        }
        return SubscriptingExpr.factory(array, idx);
    }

    @Override
    public AST visitElementSelectionExpr(LangParser.ElementSelectionExprContext ctx) {
        var expr = extractExpr(ctx.expr());
        if (expr == null) return null;
        String selection = ctx.selection.getText();
        if (expr.getType() instanceof SwizzleType) {
            int[] indices;
            try {
                indices = SwizzleUtility.swizzle(((SwizzleType) expr.getType()).getN(), selection);
            } catch (InvalidSelectionException exception) {
                this.exception = new SyntaxErrorException(ctx.selection, exception);
                return null;
            }
            return SwizzleExpr.factory(expr, indices);
        } else if (expr.getType() instanceof StructType) {
            try {
                return SelectionExpr.factory(expr, selection);
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
    public AST visitMultDivModBinaryExpr(LangParser.MultDivModBinaryExprContext ctx) {
        return extractBinaryExpr(ctx.op, ctx.expr());
    }

    @Override
    public AST visitPlusMinusBinaryExpr(LangParser.PlusMinusBinaryExprContext ctx) {
        return extractBinaryExpr(ctx.op, ctx.expr());
    }

    @Override
    public AST visitShlShrBinaryExpr(LangParser.ShlShrBinaryExprContext ctx) {
        return extractBinaryExpr(ctx.op, ctx.expr());
    }

    @Override
    public AST visitLessGreaterBinaryExpr(LangParser.LessGreaterBinaryExprContext ctx) {
        return extractBinaryExpr(ctx.op, ctx.expr());
    }

    @Override
    public AST visitEqNeqBinaryExpr(LangParser.EqNeqBinaryExprContext ctx) {
        return extractBinaryExpr(ctx.op, ctx.expr());
    }

    @Override
    public AST visitBitwiseAndBinaryExpr(LangParser.BitwiseAndBinaryExprContext ctx) {
        return extractBinaryExpr(ctx.op, ctx.expr());
    }

    @Override
    public AST visitBitwiseXorBinaryExpr(LangParser.BitwiseXorBinaryExprContext ctx) {
        return extractBinaryExpr(ctx.op, ctx.expr());
    }

    @Override
    public AST visitBitwiseOrBinaryExpr(LangParser.BitwiseOrBinaryExprContext ctx) {
        return extractBinaryExpr(ctx.op, ctx.expr());
    }

    @Override
    public AST visitLogicalAndBinaryExpr(LangParser.LogicalAndBinaryExprContext ctx) {
        return extractBinaryExpr(ctx.op, ctx.expr());
    }

    @Override
    public AST visitLogicalXorBinaryExpr(LangParser.LogicalXorBinaryExprContext ctx) {
        return extractBinaryExpr(ctx.op, ctx.expr());
    }

    @Override
    public AST visitLogicalOrBinaryExpr(LangParser.LogicalOrBinaryExprContext ctx) {
        return extractBinaryExpr(ctx.op, ctx.expr());
    }

    @Override
    public AST visitTernaryConditionalExpr(LangParser.TernaryConditionalExprContext ctx) {
        var exprs = extractExprs(ctx.expr());
        if (exprs == null) return null;
        if (!(exprs[0].getType() instanceof BoolType)) {
            this.exception = SyntaxErrorException.notBooleanExpression(ctx.expr(0).start);
            return null;
        }
        if (!exprs[1].getType().equals(exprs[2].getType())) {
            this.exception = SyntaxErrorException.cannotConvert(ctx.expr(1).start,
                    exprs[2].getType(), exprs[1].getType());
            return null;
        }
        return TernaryConditionalExpr.factory(exprs[0], exprs[1], exprs[2]);
    }

    @Override
    public AST visitAssignExpr(LangParser.AssignExprContext ctx) {
        var exprs = extractExprs(ctx.expr());
        if (exprs == null) return null;
        if (!exprs[0].isLValue()) {
            this.exception = SyntaxErrorException.lvalueRequired(ctx.start);
            return null;
        }
        if (!exprs[0].getType().equals(exprs[1].getType())) {
            this.exception = SyntaxErrorException.cannotConvert(ctx.start, exprs[1].getType(), exprs[0].getType());
            return null;
        }
        String opText = ctx.op.getText();
        BinaryOperator op = opText.equals("=") ? null :
                (BinaryOperator) Operator.fromText(opText.substring(0, opText.length() - 1));
        return new AssignmentExpr(op, exprs[0], exprs[1]);
    }

    @Override
    public AST visitParameteredExpr(LangParser.ParameteredExprContext ctx) {
        return extractExpr(ctx.expr());
    }
}
