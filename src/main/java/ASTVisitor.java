import ast.AST;
import ast.FunctionSignature;
import ast.Scope;
import ast.exceptions.*;
import ast.expr.*;
import ast.operators.*;
import ast.stmt.*;
import ast.types.*;
import ast.values.*;
import org.antlr.v4.runtime.Token;
import org.bytedeco.javacpp.annotation.Const;

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
            return BinaryExpr.factory(op, exprs);
        } catch (ArithmeticException exception) {
            this.exceptionList.add(new SyntaxErrorException(opToken, exception.getMessage()));
            return null;
        } catch (UnlocatedSyntaxErrorException exception) {
            this.exceptionList.add(new SyntaxErrorException(opToken, exception));
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
        var result = scope.lookupValue(id);
        if (result == null) {
            this.exceptionList.add(SyntaxErrorException.undeclaredID(ctx.start, id));
            return null;
        }
        if (result.value != null) {
            return new ConstExpr(result.value);
        } else if (result.stmt != null) {
            return new ReferenceExpr(result.stmt);
        } else {
            return new ParameterReferenceExpr(result.parameter);
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

        try {
            return ConstructionExpr.factory(type, exprs);
        } catch (ConstructionFailedException exception) {
            this.exceptionList.add(new SyntaxErrorException(ctx.start, exception));
            return null;
        }
    }

    @Override
    public Expr visitFunctionOrStructConstructorInvocationExpr(
            LangParser.FunctionOrStructConstructorInvocationExprContext ctx) {
        var invocationCtx = ctx.functionOrStructConstructorInvocation();
        var exprs = extractExprs(invocationCtx.expr());
        if (exprs == null) return null;

        String id = invocationCtx.structType().IDENTIFIER() == null ? null :
                invocationCtx.structType().IDENTIFIER().getText();
        if (id == null || scope.lookupStructure(id) != null) {
            try {
                Type type = Utility.typeFromStructTypeContext(invocationCtx.structType(), scope);
                type.construct(exprs);
                return ConstructionExpr.factory(type, exprs);
            } catch (SyntaxErrorException exception) {
                this.exceptionList.add(exception);
                return null;
            } catch (ConstructionFailedException exception) {
                this.exceptionList.add(new SyntaxErrorException(ctx.start, exception));
                return null;
            }
        } else {
            if (invocationCtx.structType().specifiedArrayLength() != null) {
                this.exceptionList.add(SyntaxErrorException.undeclaredID(ctx.start, id));
                return null;
            }
            var types = new Type[exprs.length];
            for (int i = 0; i < exprs.length; i++) types[i] = exprs[i].getType();
            var sig = scope.lookupFunction(id, types);
            if (sig == null) {
                this.exceptionList.add(SyntaxErrorException.notMatchFunction(ctx.start, id));
                return null;
            }
            for (int i = 0; i < exprs.length; i++) {
                var qualifier = sig.parameters.get(i).qualifier;
                if ((qualifier == FunctionSignature.ParameterQualifier.OUT
                        || qualifier == FunctionSignature.ParameterQualifier.INOUT) && !exprs[i].isLValue()) {
                    this.exceptionList.add(SyntaxErrorException.lvalueRequired(invocationCtx.expr(i).start));
                    return null;
                }
            }
            return new CallExpr(sig, exprs);
        }
    }

    @Override
    public Expr visitArraySubscriptingExpr(LangParser.ArraySubscriptingExprContext ctx) {
        var array = extractExpr(ctx.expr(0));
        if (array == null) return null;

        var idx = extractExpr(ctx.expr(1));
        if (idx == null) return null;

        try {
            return SubscriptingExpr.factory(array, idx);
        } catch (UnlocatedSyntaxErrorException exception) {
            this.exceptionList.add(new SyntaxErrorException(ctx.start, exception));
            return null;
        }
    }

    @Override
    public ConstExpr visitMemberFunctionInvocationExpr(LangParser.MemberFunctionInvocationExprContext ctx) {
        var method = ctx.method;
        if (method.expr().size() >= 1 || !method.IDENTIFIER().getText().equals("length")) {
            this.exceptionList.add(SyntaxErrorException.invalidMethod(
                    ctx.method.start, method.IDENTIFIER().getText()));
            return null;
        }
        var expr = extractExpr(ctx.expr());
        if (expr == null) return null;
        if (!(expr.getType() instanceof ArrayType)) {
            this.exceptionList.add(SyntaxErrorException.lengthOnlyArrays(ctx.start));
            return null;
        } else {
            return new ConstExpr(new IntValue(((ArrayType) expr.getType()).getN()));
        }
    }

    @Override
    public Expr visitElementSelectionExpr(LangParser.ElementSelectionExprContext ctx) {
        var expr = extractExpr(ctx.expr());
        if (expr == null) return null;
        String selection = ctx.selection.getText();
        if (expr.getType() instanceof SwizzledType) {
            int[] indices;
            try {
                indices = SwizzleUtility.swizzle(((SwizzledType) expr.getType()).getN(), selection);
                return SwizzleExpr.factory(expr, indices);
            } catch (InvalidSelectionException exception) {
                this.exceptionList.add(new SyntaxErrorException(ctx.selection, exception));
                return null;
            } catch (ConstructionFailedException exception) {
                this.exceptionList.add(new SyntaxErrorException(ctx.start, exception));
                return null;
            }
        } else if (expr.getType() instanceof StructType) {
            try {
                return SelectionExpr.factory(expr, selection);
            } catch (InvalidSelectionException exception) {
                this.exceptionList.add(new SyntaxErrorException(ctx.selection, exception));
                return null;
            }
        } else {
            this.exceptionList.add(SyntaxErrorException.invalidSelectionType(ctx.start, expr.getType()));
            return null;
        }
    }

    @Override
    public Expr visitPrefixUnaryExpr(LangParser.PrefixUnaryExprContext ctx) {
        var expr = extractExpr(ctx.expr());
        if (expr == null) return null;
        var type = expr.getType();

        String opText = ctx.op.getText();
        try {
            if (opText.equals("++") || opText.equals("--")) {
                if (type instanceof IncreasableType) {
                    var one = new ConstExpr(((IncreasableType) type).one());
                    if (opText.equals("++"))
                        return new AssignmentExpr(expr, BinaryExpr.factory(Plus.OP, new Expr[]{expr, one}));
                    else
                        return new AssignmentExpr(expr, BinaryExpr.factory(Minus.OP, new Expr[]{expr, one}));
                } else {
                    this.exceptionList.add(
                            new SyntaxErrorException(ctx.op, new OperatorCannotBeAppliedException(opText, type)));
                    return null;
                }
            } else {
                UnaryOperator op = (UnaryOperator) Operator.fromText(ctx.op.getText());
                return UnaryExpr.factory(op, expr);
            }
        } catch (UnlocatedSyntaxErrorException exception) {
            this.exceptionList.add(new SyntaxErrorException(ctx.start, exception));
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
        try {
            return TernaryConditionalExpr.factory(exprs[0], exprs[1], exprs[2]);
        } catch (UnlocatedSyntaxErrorException exception) {
            this.exceptionList.add(new SyntaxErrorException(ctx.start, exception));
            return null;
        }
    }

    @Override
    public AssignmentExpr visitAssignExpr(LangParser.AssignExprContext ctx) {
        var exprs = extractExprs(ctx.expr());
        if (exprs == null) return null;
        String opText = ctx.op.getText();
        BinaryOperator op = opText.equals("=") ? null :
                (BinaryOperator) Operator.fromText(opText.substring(0, opText.length() - 1));
        try {
            if (op == null) return new AssignmentExpr(exprs[0], exprs[1]);
            return new AssignmentExpr(exprs[0], BinaryExpr.factory(op, exprs));
        } catch (UnlocatedSyntaxErrorException exception) {
            this.exceptionList.add(new SyntaxErrorException(ctx.start, exception));
            return null;
        }
    }

    @Override
    public Expr visitParameteredExpr(LangParser.ParameteredExprContext ctx) {
        return extractExpr(ctx.expr());
    }

    // == statements ==

    public List<StmtsWrapper> extractStmtsWrappers(List<LangParser.StmtContext> stmtCtxList) {
        var result = new ArrayList<StmtsWrapper>();
        stmtCtxList.forEach(stmt -> {
            var wrapper = extractStmtsWrapper(stmt);
            if (wrapper != null) result.add(wrapper);
        });
        return this.exceptionList.isEmpty() ? result : null;
    }

    public StmtsWrapper extractStmtsWrapper(LangParser.StmtContext stmtCtx) {
        if (stmtCtx.compoundStmt() != null) scope.screwIn();
        var wrapper = (StmtsWrapper) stmtCtx.accept(this);
        if (stmtCtx.compoundStmt() != null) scope.screwOut();
        return wrapper;
    }

    public StmtsWrapper extractStmtsWrapperWithScope(LangParser.StmtContext stmtCtx) {
        scope.screwIn();
        StmtsWrapper wrapper = (StmtsWrapper) stmtCtx.accept(this);
        scope.screwOut();
        return wrapper;
    }

    public List<StmtsWrapper> extractStmtsWrappersWithScopes(List<LangParser.StmtContext> stmtCtxList) {
        var list = new ArrayList<StmtsWrapper>();
        for (var stmtContext : stmtCtxList) {
            var wrapper = extractStmtsWrapperWithScope(stmtContext);
            if (wrapper == null) return null;
            list.add(wrapper);
        }
        return list;
    }

    public List<StmtsWrapper> extractStmtsWrappersWithoutScopes(List<LangParser.StmtContext> stmtCtxList) {
        var list = new ArrayList<StmtsWrapper>();
        for (var stmtContext : stmtCtxList) {
            var wrapper = extractStmtsWrapper(stmtContext);
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
    public StmtsWrapper visitSwitchStmt(LangParser.SwitchStmtContext ctx) {
        var switchExpr = (Expr) ctx.expr().accept(this);
        if (switchExpr == null) return null;
        var switchedType = switchExpr.getType();
        if (!(switchedType instanceof IntType) && !(switchedType instanceof UintType)) {
            this.exceptionList.add(SyntaxErrorException.switchInteger(ctx.start));
            return null;
        }
        var controlFlowManager = new ControlFlowManager(false, true);
        var switchStmt = new SwitchStmt(switchExpr, controlFlowManager);
        scope.pushControlFlowManager(controlFlowManager);
        for (var itemCtx : ctx.caseItem()) {
            Long expr;
            if (itemCtx.DEFAULT() == null) {
                var visitor = new ConstantVisitor(scope);
                var value = (Value) itemCtx.expr().accept(visitor);
                if (value == null) {
                    this.exceptionList.add(visitor.exception);
                    switchStmt = null;
                    break;
                }
                if (!value.getType().equals(switchedType)) {
                    this.exceptionList.add(SyntaxErrorException.caseLabelTypeMismatch(itemCtx.start));
                    switchStmt = null;
                    break;
                }
                try {
                    expr = (long) Utility.evalValueAsIntegral(value, itemCtx.expr().start);
                } catch (SyntaxErrorException ignore) {
                    expr = null;
                }
            } else {
                expr = null;
            }
            var wrappers = extractStmtsWrappersWithoutScopes(itemCtx.stmt());
            if (wrappers == null) {
                switchStmt = null;
                break;
            }
            var wrapper = new StmtsWrapper(wrappers);
            var compoundStmt = new CompoundStmt(wrapper);
            if (!switchStmt.addCaseItem(expr, compoundStmt)) {
                this.exceptionList.add(SyntaxErrorException.duplicateCase(itemCtx.start));
                switchStmt = null;
                break;
            }
        }
        scope.popControlFlowManager();
        if (switchStmt == null) return null;
        return StmtsWrapper.singleton(switchStmt);
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
        var stmtsWrappers = extractStmtsWrappers(ctx.stmt());
        if (stmtsWrappers == null) return null;
        var stmtsWrapper = new StmtsWrapper();
        stmtsWrappers.forEach(wrapper -> stmtsWrapper.stmts.addAll(wrapper.stmts));
        if (this.exceptionList.isEmpty()) {
            return stmtsWrapper;
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
        Expr condition;
        CompoundStmt step = null, body = null;
        scope.screwIn();
        var manager = new ControlFlowManager();
        scope.pushControlFlowManager(manager);
        {
            var forLoopInitialization = ctx.forLoopInitialization();
            try {
                if (forLoopInitialization.declarationStmt() != null) {
                    initialization = new CompoundStmt(
                            Utility.declarationStmtFromCtx(forLoopInitialization.declarationStmt(), scope));
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
                var wrapper = extractStmtsWrapper(ctx.body);
                if (wrapper != null) body = new CompoundStmt(wrapper);
            } else {
                var wrapper = (StmtsWrapper) ctx.body.compoundStmt().accept(this);
                if (wrapper != null) body = new CompoundStmt(wrapper);
            }
        }
        scope.popControlFlowManager();
        scope.screwOut();
        if (!(condition.getType() instanceof BoolType))
            this.exceptionList.add(SyntaxErrorException.notBooleanExpression(ctx.forLoopCondition().start));
        if (this.exceptionList.isEmpty()) {
            return StmtsWrapper.singleton(new ForStmt(initialization, condition, step, body, manager));
        } else {
            return null;
        }
    }

    @Override
    public StmtsWrapper visitReturnStmt(LangParser.ReturnStmtContext ctx) {
        var returnType = scope.innerScopes.get(1).functionSignature.returnType;
        if (returnType instanceof VoidType) {
            if (ctx.expr() != null) {
                this.exceptionList.add(SyntaxErrorException.voidCannotReturnValue(ctx.start));
                return null;
            }
            return StmtsWrapper.singleton(new ReturnStmt());
        } else {
            if (ctx.expr() == null) {
                this.exceptionList.add(SyntaxErrorException.notReturnValue(ctx.start));
                return null;
            }
            var expr = extractExpr(ctx.expr());
            if (expr == null) return null;
            if (!expr.getType().equals(returnType)) {
                this.exceptionList.add(SyntaxErrorException.returnNotMatch(ctx.expr().start));
                return null;
            }
            return StmtsWrapper.singleton(new ReturnStmt(expr));
        }
    }

    @Override
    public StmtsWrapper visitBreakStmt(LangParser.BreakStmtContext ctx) {
        var manager = scope.loopupBreak();
        if (manager == null) {
            this.exceptionList.add(SyntaxErrorException.invalidBreak(ctx.start));
            return null;
        }
        return StmtsWrapper.singleton(new BreakStmt(manager));
    }

    @Override
    public StmtsWrapper visitContinueStmt(LangParser.ContinueStmtContext ctx) {
        var manager = scope.loopupContinue();
        if (manager == null) {
            this.exceptionList.add(SyntaxErrorException.invalidContinue(ctx.start));
            return null;
        }
        return StmtsWrapper.singleton(new ContinueStmt(manager));
    }
}
