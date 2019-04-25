import ast.AST;
import ast.Scope;
import ast.exceptions.*;
import ast.expr.*;
import ast.operators.*;
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

    private BinaryExpr extractBinaryExpr(Token opToken, List<LangParser.ExprContext> exprCtxList) {
        var exprs = extractExprs(exprCtxList);
        if (exprs == null) return null;
        BinaryOperator op = (BinaryOperator) Operator.fromText(opToken.getText());
        try {
            // check applicable
            op.apply(exprs[0].getType().getDefaultValue(), exprs[1].getType().getDefaultValue());
        } catch (OperatorCannotBeAppliedException exception) {
            this.exception = new SyntaxErrorException(opToken, exception);
            return null;
        }
        return new BinaryExpr(op, exprs);
    }

    @Override
    public AST visitLiteralExpr(LangParser.LiteralExprContext ctx) {
        var value = Utility.valueFromLiteralExprContext(ctx);
        return new ConstExpr(value);
    }

    @Override
    public AST visitReferenceExpr(LangParser.ReferenceExprContext ctx) {
        String id = ctx.IDENTIFIER().getText();
        if (scope.constants.containsKey(id))
            return new ConstExpr(scope.constants.get(id));
        else if (scope.variables.containsKey(id))
            return new ReferenceExpr(scope.variables.get(id), id);
        this.exception = SyntaxErrorException.undeclaredID(ctx.start, id);
        return null;
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
        var defaultValues = new Value[exprs.length];
        for (int i = 0; i < exprs.length; i++)
            defaultValues[i] = exprs[i].getType().getDefaultValue();
        try {
            // check syntax
            Value.constructor(type, defaultValues);
        } catch (ConstructionFailedException exception) {
            this.exception = new SyntaxErrorException(ctx.start, exception);
            return null;
        }

        return new ConstructionExpr(type, exprs);
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
            } catch (SyntaxErrorException ignore) {}
        }

        return new SubscriptingExpr(array, idx);
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
}
