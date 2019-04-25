import ast.AST;
import ast.Scope;
import ast.exceptions.*;
import ast.expr.*;
import ast.types.*;
import ast.values.*;

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
}
