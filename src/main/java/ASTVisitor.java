import ast.AST;
import ast.Scope;
import ast.exceptions.*;
import ast.expr.*;
import ast.types.Type;
import ast.values.Value;

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
}
