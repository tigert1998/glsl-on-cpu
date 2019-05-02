import ast.AST;
import ast.Scope;
import ast.exceptions.*;
import ast.expr.*;
import ast.operators.*;
import ast.stmt.*;
import ast.types.*;
import ast.values.*;
import org.antlr.v4.runtime.Token;

import java.util.*;

public class ASTVisitor extends LangBaseVisitor<AST> {
    private Scope scope;
    public List<SyntaxErrorException> exceptionList = new ArrayList<>();

    public ASTVisitor(Scope scope) {
        this.scope = scope;
    }

    private Expr[] extractExprs(List<LangParser.ExprContext> exprCtxList) {
        int total = exprCtxList.size();
        Expr[] exprs = new Expr[total];
        var visitor = new ASTVisitor(scope);
        for (int i = 0; i < total; i++) {
            exprs[i] = (Expr) exprCtxList.get(i).accept(visitor);
            if (exprs[i] == null) {
                this.exceptionList.addAll(visitor.exceptionList);
                return null;
            }
        }
        return exprs;
    }

    private Expr extractExpr(LangParser.ExprContext exprCtx) {
        var visitor = new ASTVisitor(scope);
        var expr = (Expr) exprCtx.accept(visitor);
        if (expr == null) {
            this.exceptionList.addAll(visitor.exceptionList);
        }
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
            this.exceptionList.add(new SyntaxErrorException(opToken, exception));
            return null;
        }
        try {
            return BinaryExpr.factory(op, exprs);
        } catch (ArithmeticException exception) {
            this.exceptionList.add(new SyntaxErrorException(opToken, exception.getMessage()));
            return null;
        }
    }

    @Override
    public ConstExpr visitLiteralExpr(LangParser.LiteralExprContext ctx) {
        var value = Utility.valueFromLiteralExprContext(ctx);
        return new ConstExpr(value);
    }

    @Override
    public Expr visitReferenceExpr(LangParser.ReferenceExprContext ctx) {
        String id = ctx.IDENTIFIER().getText();
        var result = scope.lookupConstantOrVariable(id);
        if (result == null) {
            this.exceptionList.add(SyntaxErrorException.undeclaredID(ctx.start, id));
            return null;
        }
        if (result.value != null) {
            return new ConstExpr(result.value);
        } else {
            return new ReferenceExpr(result.stmt);
        }
    }

    @Override
    public Expr visitBasicTypeConstructorInvocationExpr(LangParser.BasicTypeConstructorInvocationExprContext ctx) {
        var basicTypeConstructorInvocation = ctx.basicTypeConstructorInvocation();
        Type type;
        try {
            type = Utility.typeFromBasicTypeContext(basicTypeConstructorInvocation.basicType(), scope);
        } catch (SyntaxErrorException exception) {
            this.exceptionList.add(exception);
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
            this.exceptionList.add(new SyntaxErrorException(ctx.start, exception));
            return null;
        }

        return ConstructionExpr.factory(type, exprs);
    }

    @Override
    public Expr visitArraySubscriptingExpr(LangParser.ArraySubscriptingExprContext ctx) {
        var array = extractExpr(ctx.expr(0));
        if (array == null) return null;
        if (!(array.getType().getDefaultValue() instanceof Indexed)) {
            this.exceptionList.add(SyntaxErrorException.invalidSubscriptingType(ctx.start, ctx.expr(0).getText()));
            return null;
        }

        var idx = extractExpr(ctx.expr(1));
        if (idx == null) return null;
        if (!(idx.getType() instanceof IntType || idx.getType() instanceof UintType)) {
            this.exceptionList.add(SyntaxErrorException.notIntegerExpression(ctx.idx.start));
            return null;
        }

        if (idx instanceof ConstExpr) {
            try {
                int i = Utility.evalValueAsIntegral(((ConstExpr) idx).getValue(), ctx.idx.start);
                try {
                    ((Indexed) array.getType().getDefaultValue()).valueAt(i);
                } catch (InvalidIndexException exception) {
                    this.exceptionList.add(new SyntaxErrorException(ctx.idx.start, exception));
                    return null;
                }
            } catch (SyntaxErrorException ignore) {
            }
        }
        return SubscriptingExpr.factory(array, idx);
    }

    @Override
    public Expr visitElementSelectionExpr(LangParser.ElementSelectionExprContext ctx) {
        var expr = extractExpr(ctx.expr());
        if (expr == null) return null;
        String selection = ctx.selection.getText();
        if (expr.getType() instanceof SwizzleType) {
            int[] indices;
            try {
                indices = SwizzleUtility.swizzle(((SwizzleType) expr.getType()).getN(), selection);
            } catch (InvalidSelectionException exception) {
                this.exceptionList.add(new SyntaxErrorException(ctx.selection, exception));
                return null;
            }
            return SwizzleExpr.factory(expr, indices);
        } else if (expr.getType() instanceof StructType) {
            try {
                return SelectionExpr.factory(expr, selection);
            } catch (InvalidSelectionException exception) {
                this.exceptionList.add(new SyntaxErrorException(ctx.selection, exception));
                return null;
            }
        } else {
            this.exceptionList.add(SyntaxErrorException.invalidSelectionType(ctx.start, ctx.expr().getText()));
            return null;
        }
    }

    @Override
    public Expr visitMultDivModBinaryExpr(LangParser.MultDivModBinaryExprContext ctx) {
        return extractBinaryExpr(ctx.op, ctx.expr());
    }

    @Override
    public Expr visitPlusMinusBinaryExpr(LangParser.PlusMinusBinaryExprContext ctx) {
        return extractBinaryExpr(ctx.op, ctx.expr());
    }

    @Override
    public Expr visitShlShrBinaryExpr(LangParser.ShlShrBinaryExprContext ctx) {
        return extractBinaryExpr(ctx.op, ctx.expr());
    }

    @Override
    public Expr visitLessGreaterBinaryExpr(LangParser.LessGreaterBinaryExprContext ctx) {
        return extractBinaryExpr(ctx.op, ctx.expr());
    }

    @Override
    public Expr visitEqNeqBinaryExpr(LangParser.EqNeqBinaryExprContext ctx) {
        return extractBinaryExpr(ctx.op, ctx.expr());
    }

    @Override
    public Expr visitBitwiseAndBinaryExpr(LangParser.BitwiseAndBinaryExprContext ctx) {
        return extractBinaryExpr(ctx.op, ctx.expr());
    }

    @Override
    public Expr visitBitwiseXorBinaryExpr(LangParser.BitwiseXorBinaryExprContext ctx) {
        return extractBinaryExpr(ctx.op, ctx.expr());
    }

    @Override
    public Expr visitBitwiseOrBinaryExpr(LangParser.BitwiseOrBinaryExprContext ctx) {
        return extractBinaryExpr(ctx.op, ctx.expr());
    }

    @Override
    public Expr visitLogicalAndBinaryExpr(LangParser.LogicalAndBinaryExprContext ctx) {
        return extractBinaryExpr(ctx.op, ctx.expr());
    }

    @Override
    public Expr visitLogicalXorBinaryExpr(LangParser.LogicalXorBinaryExprContext ctx) {
        return extractBinaryExpr(ctx.op, ctx.expr());
    }

    @Override
    public Expr visitLogicalOrBinaryExpr(LangParser.LogicalOrBinaryExprContext ctx) {
        return extractBinaryExpr(ctx.op, ctx.expr());
    }

    @Override
    public Expr visitTernaryConditionalExpr(LangParser.TernaryConditionalExprContext ctx) {
        var exprs = extractExprs(ctx.expr());
        if (exprs == null) return null;
        if (!(exprs[0].getType() instanceof BoolType)) {
            this.exceptionList.add(SyntaxErrorException.notBooleanExpression(ctx.expr(0).start));
            return null;
        }
        if (!exprs[1].getType().equals(exprs[2].getType())) {
            this.exceptionList.add(SyntaxErrorException.cannotConvert(ctx.expr(1).start,
                    exprs[2].getType(), exprs[1].getType()));
            return null;
        }
        return TernaryConditionalExpr.factory(exprs[0], exprs[1], exprs[2]);
    }

    @Override
    public AssignmentExpr visitAssignExpr(LangParser.AssignExprContext ctx) {
        var exprs = extractExprs(ctx.expr());
        if (exprs == null) return null;
        if (!exprs[0].isLValue()) {
            this.exceptionList.add(SyntaxErrorException.lvalueRequired(ctx.start));
            return null;
        }
        if (!exprs[0].getType().equals(exprs[1].getType())) {
            this.exceptionList.add(SyntaxErrorException.cannotConvert(ctx.start, exprs[1].getType(), exprs[0].getType()));
            return null;
        }
        String opText = ctx.op.getText();
        BinaryOperator op = opText.equals("=") ? null :
                (BinaryOperator) Operator.fromText(opText.substring(0, opText.length() - 1));
        return new AssignmentExpr(op, exprs[0], exprs[1]);
    }

    @Override
    public Expr visitParameteredExpr(LangParser.ParameteredExprContext ctx) {
        return extractExpr(ctx.expr());
    }

    // == statements ==

    private List<StmtsWrapper> extractStmtsWrappers(List<LangParser.StmtContext> stmtCtxList) {
        var visitor = new ASTVisitor(scope);
        var result = new ArrayList<StmtsWrapper>();
        stmtCtxList.forEach(stmt -> {
            var wrapper = (StmtsWrapper) stmt.accept(visitor);
            if (wrapper == null) this.exceptionList.addAll(visitor.exceptionList);
            else result.add(wrapper);
        });
        return this.exceptionList.isEmpty() ? result : null;
    }

    private StmtsWrapper extractStmtsWrapper(LangParser.StmtContext stmtCtx) {
        var visitor = new ASTVisitor(scope);
        var wrapper = (StmtsWrapper) stmtCtx.accept(visitor);
        if (wrapper == null) this.exceptionList.addAll(visitor.exceptionList);
        return wrapper;
    }

    public StmtsWrapper extractStmtsWrapperWithScope(LangParser.StmtContext stmtCtx) {
        var visitor = new ASTVisitor(scope);

        if (stmtCtx.compoundStmt() == null) scope.screwIn();
        StmtsWrapper wrapper = (StmtsWrapper) stmtCtx.accept(visitor);
        if (stmtCtx.compoundStmt() == null) scope.screwOut();

        if (wrapper == null) {
            this.exceptionList.addAll(visitor.exceptionList);
            return null;
        }
        return wrapper;
    }

    private List<StmtsWrapper> extractStmtsWrappersWithScopes(List<LangParser.StmtContext> stmtCtxList) {
        var list = new ArrayList<StmtsWrapper>();
        for (var stmtContext : stmtCtxList) {
            var wrapper = extractStmtsWrapperWithScope(stmtContext);
            if (wrapper == null) return null;
            list.add(wrapper);
        }
        return list;
    }

    @Override
    public StmtsWrapper visitDeclarationStmt(LangParser.DeclarationStmtContext ctx) {
        try {
            return Utility.declarationStmtFromCtx(ctx, scope);
        } catch (SyntaxErrorException exception) {
            this.exceptionList.add(exception);
            return null;
        }
    }

    @Override
    public StmtsWrapper visitExprStmt(LangParser.ExprStmtContext ctx) {
        try {
            return Utility.exprStmtFromCtx(ctx, scope);
        } catch (SyntaxErrorException exception) {
            this.exceptionList.add(exception);
            return null;
        }
    }

    @Override
    public StmtsWrapper visitSelectionStmt(LangParser.SelectionStmtContext ctx) {
        var expr = extractExpr(ctx.expr());
        if (expr == null) return null;
        if (!(expr.getType() instanceof BoolType)) {
            this.exceptionList.add(SyntaxErrorException.notBooleanExpression(ctx.expr().start));
            return null;
        }

        var wrappers = extractStmtsWrappersWithScopes(ctx.stmt());
        if (wrappers == null) return null;

        if (expr instanceof ConstExpr) {
            var choice = ((BoolValue) ((ConstExpr) expr).getValue()).value;
            if (choice) return wrappers.get(0);
            return wrappers.size() >= 2 ? wrappers.get(1) : new StmtsWrapper();
        }

        var result = new StmtsWrapper();
        result.stmts.add(new IfStmt(expr, new CompoundStmt(wrappers.get(0)),
                wrappers.size() <= 1 ? new CompoundStmt() : new CompoundStmt(wrappers.get(1))));
        return result;
    }

    @Override
    public StmtsWrapper visitCompoundStmt(LangParser.CompoundStmtContext ctx) {
        var compoundStmt = new CompoundStmt();
        scope.screwIn();

        var stmtsWrappers = extractStmtsWrappers(ctx.stmt());
        if (stmtsWrappers == null) return null;
        stmtsWrappers.forEach(stmtsWrapper -> {
            compoundStmt.stmts.addAll(stmtsWrapper.stmts);
        });
        scope.screwOut();
        if (this.exceptionList.isEmpty()) {
            if (compoundStmt.stmts.size() == 0) return new StmtsWrapper();
            return StmtsWrapper.singleton(compoundStmt);
        }
        return null;
    }

    @Override
    public StmtsWrapper visitWhileLoopStmt(LangParser.WhileLoopStmtContext ctx) {
        try {
            return Utility.whileStmtFromCtx(ctx, scope);
        } catch (SyntaxErrorException exception) {
            this.exceptionList.add(exception);
            return null;
        }
    }

    @Override
    public StmtsWrapper visitDoWhileLoopStmt(LangParser.DoWhileLoopStmtContext ctx) {
        try {
            return Utility.doWhileStmtFromCtx(ctx, scope);
        } catch (SyntaxErrorException exception) {
            this.exceptionList.add(exception);
            return null;
        }
    }

    @Override
    public StmtsWrapper visitEmptyStmt(LangParser.EmptyStmtContext ctx) {
        return new StmtsWrapper();
    }

    @Override
    public StmtsWrapper visitForLoopStmt(LangParser.ForLoopStmtContext ctx) {
        CompoundStmt initialization = null;
        Expr condition = null;
        CompoundStmt step = null, body = null;
        scope.screwIn();
        {
            var forLoopInitialization = ctx.forLoopInitialization();
            try {
                if (forLoopInitialization.declarationStmt() != null) {
                    initialization = new CompoundStmt(Utility.declarationStmtFromCtx(forLoopInitialization.declarationStmt(), scope));
                } else if (forLoopInitialization.exprStmt() != null) {
                    initialization = new CompoundStmt(Utility.exprStmtFromCtx(forLoopInitialization.exprStmt(), scope));
                } else {
                    initialization = new CompoundStmt();
                }
            } catch (SyntaxErrorException exception) {
                this.exceptionList.add(exception);
            }
        }
        {
            if (ctx.forLoopCondition().expr() != null) {
                condition = extractExpr(ctx.forLoopCondition().expr());
            } else {
                condition = new ConstExpr(new BoolValue(true));
            }
        }
        {
            if (ctx.forLoopStep().expr() != null) {
                var expr = extractExpr(ctx.forLoopStep().expr());
                if (expr != null) {
                    step = CompoundStmt.singleton(new ExprStmt(expr));
                }
            } else {
                step = new CompoundStmt();
            }
        }
        {
            if (ctx.body.compoundStmt() == null) {
                body = new CompoundStmt(extractStmtsWrapper(ctx.body));
            } else {
                var stmtsWrappers = extractStmtsWrappers(ctx.body.compoundStmt().stmt());
                if (stmtsWrappers != null) {
                    body = new CompoundStmt();
                    for (var wrapper : stmtsWrappers)
                        body.stmts.addAll(wrapper.stmts);
                }
            }
        }
        scope.screwOut();
        if (!(condition.getType() instanceof BoolType))
            this.exceptionList.add(SyntaxErrorException.notBooleanExpression(ctx.forLoopCondition().start));
        if (this.exceptionList.isEmpty()) {
            return StmtsWrapper.singleton(new ForStmt(initialization, condition, step, body));
        } else {
            return null;
        }
    }
}
